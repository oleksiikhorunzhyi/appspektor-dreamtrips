package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.messenger.delegate.PaginationDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Status;
import com.messenger.messengerservers.entities.User;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.synchmechanism.ConnectionStatus;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.ChatSettingsActivity;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.util.OpenedConversationTracker;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class ChatScreenPresenterImpl extends MessengerPresenterImpl<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {

    private final static int MAX_MESSAGE_PER_PAGE = 20;
    private static final int UNREAD_DELAY = 2000;

    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    NotificationDelegate notificationDelegate;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    User user;
    @Inject
    OpenedConversationTracker openedConversationTracker;

    private Activity activity;
    protected PaginationDelegate paginationDelegate;
    protected ProfileCrosser profileCrosser;
    protected WeakHandler handler;
    protected ConversationHelper conversationHelper;
    protected Subscription connectivityStatusSubscription;

    protected final String conversationId;
    protected Conversation conversation;
    protected Chat chat;

    private final ConversationsDAO conversationDAO;
    private final ParticipantsDAO participantsDAO;
    private final MessageDAO messageDAO;

    protected int page = 0;
    protected long before = 0;
    protected boolean haveMoreElements = true;
    protected boolean isLoading = false;
    protected boolean pendingScroll = false;
    protected boolean typing;

    private List<User> participants;

    public ChatScreenPresenterImpl(Context context, Intent startIntent) {
        this.activity = (Activity) context;
        handler = new WeakHandler();
        participants = Collections.emptyList();

        ((Injector) context.getApplicationContext()).inject(this);
        conversationDAO = new ConversationsDAO(context.getApplicationContext());
        participantsDAO = new ParticipantsDAO(context.getApplicationContext());
        messageDAO = new MessageDAO(context.getApplicationContext());

        paginationDelegate = new PaginationDelegate(context, messengerServerFacade, MAX_MESSAGE_PER_PAGE);
        profileCrosser = new ProfileCrosser(context, routeCreator);
        conversationHelper = new ConversationHelper();

        conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);
    }

    private Observable<Chat> createChat(ChatManager chatManager, Conversation conversation) {
        return Observable.<Chat>create(subscriber -> {
            switch (conversation.getType()) {
                case Conversation.Type.CHAT:
                    try {
                        participantsDAO.getParticipant(conversationId, user.getId())
                                .subscribe(mate -> subscriber
                                                .onNext(chatManager.createSingleUserChat(mate.getId(), conversationId))
                                );
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                    break;
                case Conversation.Type.GROUP:
                default:
                    String ownerId = conversation.getOwnerId();
                    boolean isOwner = ownerId != null && ownerId.equals(user.getId());
                    subscriber.onNext(chatManager.createMultiUserChat(conversation.getId(), getUser().getId(), isOwner));
                    break;
            }
            subscriber.onCompleted();
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        connectConversation();
        openedConversationTracker.setOpenedConversation(conversationId);
        connectivityStatusSubscription = MessengerConnector.getInstance().subscribe()
                .subscribe(connectionStatus -> getView().enableSendButton(connectionStatus == ConnectionStatus.CONNECTED));
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paginationDelegate.stopPaginate();
        openedConversationTracker.setOpenedConversation(null);
        connectivityStatusSubscription.unsubscribe();
    }

    protected void connectConversation() {
        ConnectableObservable<Conversation> source = conversationDAO.getConversation(conversationId)
                .filter(conversation -> conversation != null)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .publish();
        source
                .first()
                .subscribe(this::onConversationLoadedFirstTime);
        source
                .subscribe(this::onConversationUpdated);

        source
                .flatMap(conversation1 -> createChat(messengerServerFacade.getChatManager(), conversation))
                .doOnNext(this::onChatLoaded)
                .subscribe(chat -> this.chat = chat);

        source.doOnSubscribe(() -> getView().showLoading());
        source.connect();
    }

    private void onConversationLoadedFirstTime(Conversation conversation) {
        this.conversation = conversation;
        notificationDelegate.cancel(conversation.getId().hashCode());
        ((AppCompatActivity) activity).supportInvalidateOptionsMenu();
        //
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        connectMembers();
        connectMessages();
        loadNextPage();
    }

    private void onConversationUpdated(Conversation conversation) {
        this.conversation = conversation;
        updateConversationInfo();
    }

    private void updateConversationInfo() {
        getView().setTitle(conversation, participants);
        getView().showUnreadMessageCount(conversation.getUnreadMessageCount());
    }

    private void onChatLoaded(Chat chat) {
        chat.addOnChatStateListener((state, userId) -> {
            ChatScreen view = getView();
            if (view == null) return;
            final User user = UsersDAO.getUser(userId);
            switch (state) {
                case Composing:
                    handler.post(() -> view.addTypingUser(user));
                    break;
                case Paused:
                    handler.post(() -> view.removeTypingUser(user));
                    break;
            }
        });
    }

    protected void connectMembers() {
        participantsDAO.getParticipants(conversationId)
                .map(c -> SqlUtils.convertToList(User.class, c))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
                .subscribe(members -> {
                    participants = members;
                    getView().setTitle(conversation, participants);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Messages
    ///////////////////////////////////////////////////////////////////////////

    private void connectMessages() {
        messageDAO.getMessage(conversationId)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
                .subscribe(cursor -> {
                    getView().showMessages(cursor, conversation, pendingScroll);
                    pendingScroll = false;
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Message Pagination
    ///////////////////////////////////////////////////////////////////////////

    private void loadNextPage() {
        isLoading = true;
        ChatLayoutViewState viewState = getViewState();
        if (!haveMoreElements || viewState.getLoadingState() == ChatLayoutViewState.LoadingState.LOADING)
            return;

        getView().showLoading();
        viewState.setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
        paginationDelegate.loadConversationHistoryPage(conversation, ++page, before,
                this::paginationPageLoaded,
                this::showContent);
    }

    private void paginationPageLoaded(int loadedPage, List<Message> loadedMessages) {
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        isLoading = false;
        if (loadedMessages == null || loadedMessages.size() == 0) {
            haveMoreElements = false;
            showContent();
            return;
        }

        int loadedCount = loadedMessages.size();
        haveMoreElements = loadedCount == MAX_MESSAGE_PER_PAGE;
        Message lastMessage = loadedMessages.get(loadedCount - 1);

        before = lastMessage.getDate().getTime();
        showContent();
    }

    @Override
    public void firstVisibleMessageChanged(Message firstVisibleMessage) {
        if (firstVisibleMessage.isRead()) return;

        sendAndMarkChatEntities(firstVisibleMessage);
    }

    @Override
    public void onNextPageReached() {
        if (!isLoading && conversation != null) loadNextPage();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unread and Mark as read
    ///////////////////////////////////////////////////////////////////////////

    private void showUnreadMessageCount() {
        handler.postDelayed(() -> {
            ChatScreen view = getView();
            if (view != null) view.showUnreadMessageCount(conversation.getUnreadMessageCount());
        }, UNREAD_DELAY);
    }

    private void updateUnreadMessageCount(Message firstMessage) {
        messageDAO.unreadCount(conversationId, firstMessage.getDate().getTime())
                .subscribe(unreadCount -> {
                    conversation.setUnreadMessageCount(unreadCount);
                    conversation.save();
                });
    }

    private void sendAndMarkChatEntities(Message firstMessage) {
        chat.changeMessageStatus(firstMessage, Status.DISPLAYED);

        markMessagesAsRead(firstMessage);
        updateUnreadMessageCount(firstMessage);
        showUnreadMessageCount();
    }

    private void markMessagesAsRead(Message firstMessage) {
        messageDAO.markMessagesAsRead(conversationId, firstMessage.getDate().getTime()).subscribe();
    }

    ///////////////////////////////////////////////////////////////////////////
    // New message
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void messageTextChanged(int length) {
        if (!typing && length > 0) {
            typing = true;
            chat.setCurrentState(ChatState.Composing);
        } else if (length == 0) {
            typing = false;
            chat.setCurrentState(ChatState.Paused);
        }
    }

    @Override
    public boolean onNewMessageFromUi(String message) {
        if (TextUtils.getTrimmedLength(message) == 0) {
            Toast.makeText(activity, R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (conversation == null) return false;

        try {
            pendingScroll = true;
            chat.sendMessage(new Message.Builder()
                    .locale(Locale.getDefault())
                    .text(message)
                    .from(user.getId())
                    .build());
        } catch (ConnectionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // User
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void openUserProfile(User user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public User getUser() {
        return user;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // hide button for adding user for not owners of group chats
        if (conversation == null) {
            menu.findItem(R.id.action_add).setVisible(false);
            menu.findItem(R.id.action_settings).setVisible(false);
            return;
        } else {
            menu.findItem(R.id.action_add).setVisible(true);
            menu.findItem(R.id.action_settings).setVisible(true);
        }

        if (conversationHelper.isGroup(conversation)) {
            boolean owner = user.getId().equals(conversation.getOwnerId());
            menu.findItem(R.id.action_add).setVisible(owner);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                NewChatMembersActivity.startInAddMembersMode(activity, conversation.getId());
                return true;
            case R.id.action_settings:
                if (conversationHelper.isGroup(conversation)) {
                    ChatSettingsActivity.startGroupChatSettings(activity, conversation.getId());
                } else {
                    ChatSettingsActivity.startSingleChatSettings(activity, conversation.getId());
                }
                return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // State
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onNewViewState() {
        state = new ChatLayoutViewState();
        state.setLoadingState(conversation == null ?
                ChatLayoutViewState.LoadingState.LOADING : ChatLayoutViewState.LoadingState.CONTENT);
    }

    @Override
    public void applyViewState() {
        if (!isViewAttached()) return;

        switch (getViewState().getLoadingState()) {
            case LOADING:
                getView().showLoading();
                break;
            case CONTENT:
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
    }

    private void showContent() {
        ChatScreen screen = getView();
        if (screen == null) return;
        screen.getActivity().runOnUiThread(screen::showContent);
    }

}
