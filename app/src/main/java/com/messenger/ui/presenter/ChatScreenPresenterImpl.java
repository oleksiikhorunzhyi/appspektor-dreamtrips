package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.messenger.delegate.PaginationDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
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
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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
    UsersDAO usersDAO;
    @Inject
    OpenedConversationTracker openedConversationTracker;

    private Activity activity;
    protected PaginationDelegate paginationDelegate;
    protected ProfileCrosser profileCrosser;
    protected WeakHandler handler;
    protected ConversationHelper conversationHelper;

    protected final String conversationId;
    protected Conversation conversation;

    @Inject
    ConversationsDAO conversationDAO;
    @Inject
    MessageDAO messageDAO;
    @Inject
    ParticipantsDAO participantsDAO;

    protected int page = 0;
    protected long before = 0;
    protected boolean haveMoreElements = true;
    protected boolean isLoading = false;
    protected boolean pendingScroll = false;
    protected boolean typing;

    private List<User> participants;
    private Observable<Chat> chatObservable;

    public ChatScreenPresenterImpl(Context context, Intent startIntent) {
        this.activity = (Activity) context;
        handler = new WeakHandler();
        participants = Collections.emptyList();

        ((Injector) context.getApplicationContext()).inject(this);

        paginationDelegate = new PaginationDelegate(context, messengerServerFacade, MAX_MESSAGE_PER_PAGE);
        profileCrosser = new ProfileCrosser(context, routeCreator);
        conversationHelper = new ConversationHelper();

        conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);
    }

    private Observable<Chat> createChat(ChatManager chatManager, Conversation conversation) {
        switch (conversation.getType()) {
            case Conversation.Type.CHAT:
                return participantsDAO
                        .getParticipant(conversation.getId(), user.getId()).first()
                        .map(mate -> chatManager.createSingleUserChat(mate.getId(), conversation.getId()));
            case Conversation.Type.GROUP:
            default:
                boolean isOwner = conversationHelper.isOwner(conversation, user);
                return Observable.defer(() -> Observable.just(chatManager.createMultiUserChat(conversation.getId(), user.getId(), isOwner)));
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        connectConversation();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        openedConversationTracker.setOpenedConversation(visibility == View.VISIBLE ? conversationId : null);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paginationDelegate.stopPaginate();
    }

    private void connectConversation() {
        ConnectableObservable<Conversation> source = conversationDAO.getConversation(conversationId)
                .onBackpressureLatest()
                .filter(conversation -> conversation != null)
                .compose(new IoToMainComposer<>())
                .compose(bindView())
                .publish();
        source
                .first()
                .subscribe(this::onConversationLoadedFirstTime);
        source
                .subscribe(this::onConversationUpdated);

        chatObservable = source
                .flatMap(conv -> createChat(messengerServerFacade.getChatManager(), conv))
                .replay(1).autoConnect();

        submitOneChatAction(this::onChatLoaded);

        source.doOnSubscribe(() -> getView().showLoading());
        source.connect();
    }

    private void onConversationLoadedFirstTime(Conversation conversation) {
        this.conversation = conversation;
        notificationDelegate.cancel(conversation.getId().hashCode());
        ((AppCompatActivity) activity).supportInvalidateOptionsMenu();
        //
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        subscribeUnreadMessageCount(conversation);
        connectMembers(conversation);
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

            usersDAO.getUserById(userId)
                    .first()
                    .compose(bindView())
                    .compose(new IoToMainComposer<>())
                    .subscribe(user -> {
                        switch (state) {
                            case Composing:
                                view.addTypingUser(user);
                                break;
                            case Paused:
                                view.removeTypingUser(user);
                                break;
                        }
                    });
        });
    }

    protected void connectMembers(Conversation conversation) {
        participantsDAO.getParticipants(conversationId)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(c -> SqlUtils.convertToList(User.class, c))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(members -> {
                    participants = members;
                    getView().setTitle(conversation, participants);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Messages
    ///////////////////////////////////////////////////////////////////////////

    private void connectMessages() {
        messageDAO.getMessages(conversationId)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
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
        // not outgoing and unread
        if (!TextUtils.equals(user.getId(), firstVisibleMessage.getFromId()) && firstVisibleMessage.getStatus() == Message.Status.SENT) {
            sendAndMarkChatEntities(firstVisibleMessage);
        }
    }

    @Override
    public void onNextPageReached() {
        if (!isLoading && conversation != null) loadNextPage();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unread and Mark as read
    ///////////////////////////////////////////////////////////////////////////

    private void showUnreadMessageCount(int unreadMessageCount) {
        Observable.just(true)
                .delay(UNREAD_DELAY, TimeUnit.MILLISECONDS)
                .compose(new IoToMainComposer<>())
                .compose(bindView())
                .subscribe(o -> getView().showUnreadMessageCount(conversation.getUnreadMessageCount()));
    }

    private void subscribeUnreadMessageCount(Conversation conversation) {
        messageDAO.unreadCount(conversationId, user.getId())
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
                .doOnNext(unreadCount -> {
                    if (conversation.getUnreadMessageCount() != unreadCount) {
                        conversation.setUnreadMessageCount(unreadCount);
                        conversation.save();
                    }
                })
                .subscribe(this::showUnreadMessageCount);
    }

    private void sendAndMarkChatEntities(Message firstMessage) {
        chatObservable.first()
                .flatMap(chat -> chat.sendReadStatus(firstMessage).flatMap(this::markMessagesAsRead))
                .compose(new IoToMainComposer<>())
                .doOnNext(m -> Timber.i("Message marked as read %s", m))
                .subscribe();
    }

    private Observable<Message> markMessagesAsRead(Message firstIncomingMessage) {
        //message does not contain toId
        return messageDAO.markMessagesAsRead(conversationId,
                user.getId(), firstIncomingMessage.getDate().getTime())
                .map(integer -> firstIncomingMessage);
    }

    ///////////////////////////////////////////////////////////////////////////
    // New message
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void messageTextChanged(int length) {
        if (!isConnectionPresent()) return;

        submitOneChatAction(chat -> {
            if (!typing && length > 0) {
                typing = true;
                chat.setCurrentState(ChatState.Composing);
            } else if (length == 0) {
                typing = false;
                chat.setCurrentState(ChatState.Paused);
            }
        });
    }

    @Override
    public boolean sendMessage(String message) {
        if (TextUtils.getTrimmedLength(message) == 0) {
            Toast.makeText(activity, R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        submitOneChatAction(chat -> {
            chat.send(new Message.Builder()
                            .locale(Locale.getDefault())
                            .text(message)
                            .from(user.getId())
                            .build()
            )
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        });
        return true;
    }

    @Override
    public void retrySendMessage(String messageId) {
        submitOneChatAction(chat -> {
            messageDAO.getMessage(messageId)
                    .flatMap(chat::send)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        });
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

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private void submitOneChatAction(Action1<Chat> action1){
        chatObservable
                .first()
                .compose(bindView())
                .subscribe(action1);
    }
}
