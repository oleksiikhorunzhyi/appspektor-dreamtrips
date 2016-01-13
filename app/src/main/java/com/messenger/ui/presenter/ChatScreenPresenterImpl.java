package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.messenger.delegate.PaginationDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Message$Table;
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

import java.util.List;
import java.util.Locale;

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
    private static final int MARK_AS_READ_DELAY_FOR_SCROLL_EVENTS = 2000;
    private static final int MARK_AS_READ_DELAY_SINCE_SCREEN_OPENED = 2000;

    @Inject
    User user;
    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    NotificationDelegate notificationDelegate;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    OpenedConversationTracker openedConversationTracker;

    private Activity activity;
    protected PaginationDelegate paginationDelegate;
    protected ProfileCrosser profileCrosser;
    protected ConversationHelper conversationHelper;

    protected final String conversationId;

    @Inject
    ConversationsDAO conversationDAO;
    @Inject
    UsersDAO usersDAO;
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
    private long screenOpenedTimestamp;

    private Observable<Chat> chatObservable;
    private Observable<Conversation> conversationObservable;

    private Handler handler = new Handler();
    boolean skipNextMessagesUiDueToPendingChangesInDb = false;

    public ChatScreenPresenterImpl(Context context, Intent startIntent) {
        this.activity = (Activity) context;

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
        screenOpenedTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        openedConversationTracker.setOpenedConversation(visibility == View.VISIBLE ? conversationId : null);
        if (visibility == View.GONE) {
            handler.removeCallbacksAndMessages(null);
        }
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
                .compose(bindViewIoToMainComposer())
                .publish();

        conversationObservable = source
                .replay(1)
                .autoConnect();

        conversationObservable
                .first()
                .subscribe(this::onConversationLoadedFirstTime);

        conversationObservable
                .subscribe(conversation ->
                        getView().showUnreadMessageCount(conversation.getUnreadMessageCount()));

        chatObservable = source
                .flatMap(conv -> createChat(messengerServerFacade.getChatManager(), conv))
                .replay(1).autoConnect();

        submitOneChatAction(this::onChatLoaded);

        source.doOnSubscribe(() -> getView().showLoading());
        source.connect();
    }

    private void onConversationLoadedFirstTime(Conversation conversation) {
        notificationDelegate.cancel(conversation.getId().hashCode());
        ((AppCompatActivity) activity).supportInvalidateOptionsMenu();
        //
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        subscribeUnreadMessageCount(conversation);
        connectMembers(conversation);
        connectMessages(conversation);
        loadNextPage();
    }

    private void onChatLoaded(Chat chat) {
        Observable.<Pair<ChatState, String>>create(subscriber ->
                chat.addOnChatStateListener((state, userId) -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(new Pair<>(state, userId));
                        subscriber.onCompleted();
                    }
                }))
                .compose(bindVisibilityIoToMainComposer())
                .subscribe(chatStateStringPair ->
                                usersDAO.getUserById(chatStateStringPair.second)
                                        .first()
                                        .compose(bindVisibilityIoToMainComposer())
                                        .subscribe(user -> {
                                            switch (chatStateStringPair.first) {
                                                case Composing:
                                                    getView().addTypingUser(user);
                                                    break;
                                                case Paused:
                                                    getView().removeTypingUser(user);
                                                    break;
                                            }
                                        })
                );
    }

    protected void connectMembers(Conversation conversation) {
        participantsDAO.getParticipants(conversationId)
                .onBackpressureLatest()
                .map(c -> SqlUtils.convertToList(User.class, c))
                .compose(bindViewIoToMainComposer())
                .subscribe(members -> getView().setTitle(conversation, members));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Messages
    ///////////////////////////////////////////////////////////////////////////

    private void connectMessages(Conversation conversation) {
        messageDAO.getMessages(conversationId)
                .onBackpressureLatest()
                .filter(cursor -> cursor.getCount() > 0)
                .compose(bindViewIoToMainComposer())
                .subscribe(cursor -> {
                    skipNextMessagesUiDueToPendingChangesInDb = false;

                    Cursor oldCursor = getView().getCurrentMessagesCursor();
                    int diff = 0;
                    int count = cursor.getCount();

                    if (oldCursor != null) {
                        diff = Math.max(0, count - oldCursor.getCount());
                    }

                    // calculate future position of visible elements after cursor is updated in view
                    int firstPos = getView().getFirstVisiblePosition() + diff;
                    int lastPos = getView().getLastVisiblePosition() + diff;

                    if (oldCursor != null && count >= firstPos
                            && count - 1 <= lastPos) {
                        // mark all messages in visible window as read right away
                        for (; firstPos <= lastPos; firstPos++) {
                            cursor.moveToPosition(firstPos);
                            int status = cursor.getInt(cursor.getColumnIndex(Message$Table.STATUS));
                            if (status == Message.Status.SENT) {
                                Message m = SqlUtils.convertToModel(true, Message.class, cursor);
                                long markAsReadDelay = 0;
                                Runnable markAsReadRunnable = ()->sendAndMarkChatEntities(m);
                                if (timeSinceOpenedScreen() < MARK_AS_READ_DELAY_SINCE_SCREEN_OPENED){
                                    markAsReadDelay = MARK_AS_READ_DELAY_FOR_SCROLL_EVENTS - timeSinceOpenedScreen();
                                    handler.postDelayed(markAsReadRunnable, markAsReadDelay);
                                    skipNextMessagesUiDueToPendingChangesInDb = false;
                                } else {
                                    markAsReadRunnable.run();
                                    // avoid applying new messages with outdated statuses right away
                                    // to prevent blinking
                                    skipNextMessagesUiDueToPendingChangesInDb = true;
                                }
                            }
                        }
                    }

                    if (!skipNextMessagesUiDueToPendingChangesInDb) {
                        getView().showMessages(cursor, conversation, pendingScroll);
                        pendingScroll = false;
                    }
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
        conversationObservable
                .first()
                .compose(bindViewIoToMainComposer())
                .subscribe(conversation -> paginationDelegate.loadConversationHistoryPage(conversation,
                        ++page, before, this::paginationPageLoaded, this::showContent));

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
            handler.postDelayed(()
                    -> sendAndMarkChatEntities(firstVisibleMessage), MARK_AS_READ_DELAY_FOR_SCROLL_EVENTS);
        }
    }

    @Override
    public void onNextPageReached() {
        conversationObservable
                .first()
                .filter(conversation -> !isLoading)
                .compose(bindViewIoToMainComposer())
                .subscribe(conversation -> loadNextPage());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unread and Mark as read
    ///////////////////////////////////////////////////////////////////////////

    private void showUnreadMessageCount(int unreadMessageCount) {
        conversationObservable
                .first()
                .compose(bindViewIoToMainComposer())
                .subscribe(conversation -> {
                    getView().showUnreadMessageCount(conversation.getUnreadMessageCount());
                });
    }

    private void subscribeUnreadMessageCount(Conversation conversation) {
        messageDAO.unreadCount(conversationId, user.getId())
                .onBackpressureLatest()
                .compose(bindVisibilityIoToMainComposer())
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
        conversationObservable
                .lastOrDefault(null)
                .compose(bindVisibilityIoToMainComposer())
                .subscribe(conversation -> {
                    if (conversation == null) {
                        menu.findItem(R.id.action_add).setVisible(false);
                        menu.findItem(R.id.action_settings).setVisible(false);
                    }
                });

        conversationObservable
                .first()
                .compose(bindVisibilityIoToMainComposer())
                .subscribe(conversation -> {
                    if (conversationHelper.isGroup(conversation)) {
                        // hide button for adding user for not owners of group chats
                        boolean owner = user.getId().equals(conversation.getOwnerId());
                        menu.findItem(R.id.action_add).setVisible(owner);
                    } else {
                        menu.findItem(R.id.action_add).setVisible(true);
                    }
                    menu.findItem(R.id.action_settings).setVisible(true);
                });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                NewChatMembersActivity.startInAddMembersMode(activity, conversationId);
                return true;
            case R.id.action_settings:
                conversationObservable
                        .first()
                        .compose(bindViewIoToMainComposer())
                        .subscribe(conversation -> {
                            if (conversationHelper.isGroup(conversation)) {
                                ChatSettingsActivity.startGroupChatSettings(activity, conversationId);
                            } else {
                                ChatSettingsActivity.startSingleChatSettings(activity, conversationId);
                            }
                        });
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

        if (conversationObservable == null) {
            state.setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
            return;
        }

        conversationObservable
                .lastOrDefault(null)
                .compose(bindVisibilityIoToMainComposer())
                .subscribe(conversation -> state.setLoadingState(conversation == null ?
                        ChatLayoutViewState.LoadingState.LOADING : ChatLayoutViewState.LoadingState.CONTENT));
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

    private long timeSinceOpenedScreen() {
        return System.currentTimeMillis() - screenOpenedTimestamp;
    }
}
