package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.delegate.AttachmentDelegate;
import com.messenger.delegate.PaginationDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.notification.MessengerNotificationFactory;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.PhotoPickerDelegate;
import com.messenger.ui.view.add_member.ExistingChatPath;
import com.messenger.ui.view.chat.ChatScreen;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.settings.GroupSettingsPath;
import com.messenger.ui.view.settings.SingleSettingsPath;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.util.OpenedConversationTracker;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class ChatScreenPresenterImpl extends MessengerPresenterImpl<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {

    private final static int MAX_MESSAGE_PER_PAGE = 20;
    private static final int MARK_AS_READ_DELAY_FOR_SCROLL_EVENTS = 2000;
    private static final int MARK_AS_READ_DELAY_SINCE_MESSAGES_UI_INITIALIZED = 2000;
    private final GlobalEventEmitter messengerGlobalEmitter;

    @Inject
    DataUser user;
    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    BackStackDelegate backStackDelegate;
    @Inject
    NotificationDelegate notificationDelegate;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    OpenedConversationTracker openedConversationTracker;
    @Inject
    AttachmentDelegate attachmentDelegate;

    protected PaginationDelegate paginationDelegate;
    protected ProfileCrosser profileCrosser;
    protected ConversationHelper conversationHelper;

    protected String conversationId;

    @Inject
    ConversationsDAO conversationDAO;
    @Inject
    UsersDAO usersDAO;
    @Inject
    MessageDAO messageDAO;
    @Inject
    ParticipantsDAO participantsDAO;
    @Inject
    AttachmentDAO attachmentDAO;

    protected int page = 0;
    protected long before = 0;
    protected boolean haveMoreElements = true;
    protected boolean isLoading = false;
    protected boolean pendingScroll = false;
    protected boolean typing;
    private long messagesUiWasInitializedTimestamp;

    // // TODO: 1/28/16 replace this fields with new logic based on synctime
    private boolean unreadMessagesCounterShown = false;
    private boolean isInitialUnreadMessagesLoading = false;
    private int initialConversationUnreadMessagesCount;

    private Observable<Chat> chatObservable;
    private Observable<DataConversation> conversationObservable;
    private PublishSubject<ChatChangeStateEvent> chatStateStream;

    private Handler handler = new Handler();
    boolean skipNextMessagesUiDueToPendingChangesInDb = false;

    private PhotoPickerDelegate photoPickerDelegate;

    public ChatScreenPresenterImpl(Context context, String conversationId) {
        super(context);
        this.conversationId = conversationId;

        ((Injector) context.getApplicationContext()).inject(this);

        messengerGlobalEmitter = messengerServerFacade.getGlobalEventEmitter();
        backStackDelegate.setListener(() -> !isViewAttached() || getView().onBackPressed());
        paginationDelegate = new PaginationDelegate(messengerServerFacade, messageDAO, attachmentDAO, MAX_MESSAGE_PER_PAGE);
        profileCrosser = new ProfileCrosser(context, routeCreator);
        conversationHelper = new ConversationHelper();
        //
        chatStateStream = PublishSubject.<ChatChangeStateEvent>create();
    }

    private Observable<Chat> createChat(ChatManager chatManager, DataConversation conversation) {
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                return participantsDAO
                        .getParticipant(conversation.getId(), user.getId()).compose(new NonNullFilter<>()).first()
                        .map(mate -> chatManager.createSingleUserChat(mate.getId(), conversation.getId()));
            case ConversationType.GROUP:
            default:
                boolean isOwner = conversationHelper.isOwner(conversation, user);
                return Observable.defer(() -> Observable.just(chatManager.createMultiUserChat(conversation.getId(), user.getId(), isOwner)));
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        connectConversation();
        connectToPhotoPicker();
        connectToPendingAttachments();
        messagesUiWasInitializedTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            openedConversationTracker.addOpenedConversation(conversationId);
        } else {
            openedConversationTracker.removeOpenedConversation(conversationId);
            handler.removeCallbacksAndMessages(null);
            return;
        }

        connectTypingStartAction();
        connectTypingStopAction();
    }

    private void connectTypingStartAction() {
        getView().getEditMessageObservable()
                .skip(1)
                .filter(textViewTextChangeEvent -> textViewTextChangeEvent.count() > 0)
                .compose(bindVisibility())
                .throttleFirst(1, TimeUnit.SECONDS)
                .filter(textViewTextChangeEvent -> !typing)
                .flatMap(textViewTextChangeEvent -> chatObservable.first())
                .subscribe(chat -> {
                    typing = true;
                    chat.setCurrentState(ChatState.COMPOSING);
                });
    }


    private void connectTypingStopAction() {
        getView().getEditMessageObservable()
                .compose(bindVisibility())
                .skip(1)
                .debounce(2, TimeUnit.SECONDS)
                .flatMap(textViewTextChangeEvent -> chatObservable.first())
                .subscribe(chat -> {
                    typing = false;
                    chat.setCurrentState(ChatState.PAUSE);
                });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paginationDelegate.stopPaginate();
        backStackDelegate.setListener(null);
        disconnectFromPhotoPicker();
    }

    private void connectConversation() {
        ConnectableObservable<DataConversation> source = conversationDAO.getConversation(conversationId)
                .onBackpressureLatest()
                .filter(conversation -> conversation != null)
                .filter(conversation -> {
                    if (TextUtils.equals(conversation.getStatus(), ConversationStatus.PRESENT)) {
                        return true;
                    } else {
                        //if we were kicked from conversation
                        Flow.get(getContext()).set(ConversationsPath.MASTER_PATH);
                        return false;
                    }
                })
                .compose(bindViewIoToMainComposer())
                .publish();

        conversationObservable = source
                .replay(1)
                .autoConnect();

        conversationObservable
                .first()
                .subscribe(this::onConversationLoadedFirstTime);

        chatObservable = source
                .flatMap(c -> createChat(messengerServerFacade.getChatManager(), c).subscribeOn(Schedulers.io()))
                .replay(1).autoConnect();

        initUnreadMessageCounterObservables();
        submitOneChatAction(this::onChatLoaded);
        connectMembers();

        source.doOnSubscribe(() -> getView().showLoading());
        source.connect();
    }

    private void initUnreadMessageCounterObservables() {
        Observable<Integer> unreadMessageCounterObservable = conversationObservable.map(c -> c.getUnreadMessageCount());

        unreadMessageCounterObservable
                .filter(count -> !isInitialUnreadMessagesLoading && !unreadMessagesCounterShown)
                .subscribe(value -> {
                    unreadMessagesCounterShown = true;
                    getView().showUnreadMessageCount(value);
                });

        unreadMessageCounterObservable
                .filter(count -> unreadMessagesCounterShown && count == 0)
                .subscribe(value -> getView().hideUnreadMessageCount());
    }

    private void onConversationLoadedFirstTime(DataConversation conversation) {
        notificationDelegate.cancel(MessengerNotificationFactory.MESSENGER_TAG);
        //
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        subscribeUnreadMessageCount(conversation);
        connectMessages(conversation);
        initPagination(conversation);
    }

    private void onChatLoaded(Chat chat) {
        final OnChatStateChangedListener listener = (conversationId, userId, state) -> {
            chatStateStream.onNext(new ChatChangeStateEvent(userId, conversationId, state));
        };
        messengerGlobalEmitter.addOnChatStateChangedListener(listener);

        chatStateStream.asObservable()
                .filter(chatChangeStateEvent -> TextUtils.equals(chatChangeStateEvent.conversationId, conversationId))
                .compose(bindVisibilityIoToMainComposer())
                .doOnUnsubscribe(() -> messengerGlobalEmitter.removeOnChatStateChangedListener(listener))
                .subscribe(stateEvent -> usersDAO.getUserById(stateEvent.userId)
                                .first().compose(new NonNullFilter<>())
                                .compose(bindVisibilityIoToMainComposer())
                                .subscribe(user -> {
                                    switch (stateEvent.state) {
                                        case ChatState.COMPOSING:
                                            getView().addTypingUser(user);
                                            break;
                                        case ChatState.PAUSE:
                                            getView().removeTypingUser(user);
                                            break;
                                    }
                                })
                );
    }

    protected void connectMembers() {
        Observable<List<DataUser>> participantCursorObservable = participantsDAO.getParticipants(conversationId)
                .onBackpressureLatest()
                .map(c -> SqlUtils.convertToList(DataUser.class, c))
                .compose(bindViewIoToMainComposer());

        Observable.combineLatest(conversationObservable, participantCursorObservable,
                (con, cursor) -> new Pair<>(con, cursor))
                .subscribe(pair -> getView().setTitle(pair.first, pair.second));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Messages
    ///////////////////////////////////////////////////////////////////////////

    private void connectMessages(DataConversation conversation) {
        messageDAO.getMessages(conversationId)
                .onBackpressureLatest()
                .filter(cursor -> cursor.getCount() > 0)
                .compose(bindViewIoToMainComposer())
                .subscribe(cursor -> {
                    skipNextMessagesUiDueToPendingChangesInDb = false;

                    if (!isInitialUnreadMessagesLoading) {
                        Cursor oldCursor = getView().getCurrentMessagesCursor();
                        int diff = 0;
                        int count = cursor.getCount();
                        if (oldCursor != null) {
                            diff = Math.max(0, count - oldCursor.getCount());
                        }
                        int lastVisiblePosition = getView().getLastVisiblePosition() + diff;
                        if (lastVisiblePosition >= 0 && oldCursor != null && lastVisiblePosition < count) {
                            cursor.moveToPosition(lastVisiblePosition);
                            int status = cursor.getInt(cursor.getColumnIndex(DataMessage$Table.STATUS));
                            String id = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
                            if (status == MessageStatus.SENT && !id.equals(user.getId())) {
                                DataMessage m = SqlUtils.convertToModel(true, DataMessage.class, cursor);
                                if (timeSinceMessagesUiInitialized() < MARK_AS_READ_DELAY_SINCE_MESSAGES_UI_INITIALIZED) {
                                    long markAsReadDelay = MARK_AS_READ_DELAY_FOR_SCROLL_EVENTS - timeSinceMessagesUiInitialized();
                                    handler.postDelayed(() -> sendAndMarkChatEntities(m), markAsReadDelay);
                                    skipNextMessagesUiDueToPendingChangesInDb = false;
                                } else {
                                    sendAndMarkChatEntities(m);
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

    private void initPagination(DataConversation conversation) {
        int localUnreadMessagesCount = messageDAO.unreadCount(conversationId, user.getId())
                .toBlocking().first();
        initialConversationUnreadMessagesCount = conversation.getUnreadMessageCount();
        int localUnreadMessagesCountDiff = initialConversationUnreadMessagesCount - localUnreadMessagesCount;
        if (localUnreadMessagesCountDiff > 0) {
            isInitialUnreadMessagesLoading = true;
        }
        loadNextPage();
    }

    private void loadNextPage() {
        if (!haveMoreElements || isLoading)
            return;

        isLoading = true;

        getView().showLoading();
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
        conversationObservable
                .first()
                .compose(bindViewIoToMainComposer())
                .subscribe(conversation -> paginationDelegate.loadConversationHistoryPage(conversation,
                        ++page, before, this::paginationPageLoaded, this::showContent));

    }

    private void paginationPageLoaded(int loadedPage, List<com.messenger.messengerservers.model.Message> loadedMessages) {
        isLoading = false;
        if (loadedMessages == null || loadedMessages.size() == 0) {
            haveMoreElements = false;
        } else {
            int loadedCount = loadedMessages.size();
            haveMoreElements = loadedCount == MAX_MESSAGE_PER_PAGE;
            com.messenger.messengerservers.model.Message lastMessage = loadedMessages.get(loadedCount - 1);
            before = lastMessage.getDate();
        }

        if (isInitialUnreadMessagesLoading) {
            int localUnreadMessagesCount = messageDAO.unreadCount(conversationId, user.getId())
                    .toBlocking().first();
            int localUnreadMessagesCountDiff = initialConversationUnreadMessagesCount
                    - localUnreadMessagesCount;
            if (localUnreadMessagesCountDiff > 0) {
                // We haven't reached conversation unread count - load next page.
                // Call with handler because this callback is called on Smack thread and UI thread is required
                // conversation observable
                handler.post(() -> loadNextPage());
                // don't hide loading UI by showing content as we are still loading
                return;
            }
            isInitialUnreadMessagesLoading = false;
            messagesUiWasInitializedTimestamp = System.currentTimeMillis();
        }
        showContent();
    }

    @Override
    public void onLastVisibleMessageChanged(int position) {
        if (!isInitialUnreadMessagesLoading) {
            markAsReadWithMessagePosition(getView().getCurrentMessagesCursor(),
                    position, MARK_AS_READ_DELAY_FOR_SCROLL_EVENTS);
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

    private void subscribeUnreadMessageCount(DataConversation conversation) {
        messageDAO.unreadCount(conversationId, user.getId())
                .onBackpressureLatest()
                .compose(bindVisibilityIoToMainComposer())
                .doOnNext(unreadCount -> {
                    if (conversation.getUnreadMessageCount() != unreadCount) {
                        conversation.setUnreadMessageCount(unreadCount);
                        conversation.save();
                    }
                })
                .subscribe();
    }

    private void markAsReadWithMessagePosition(Cursor cursor, int position, long delay) {
        if (cursor == null || cursor.isClosed() || !cursor.moveToPosition(position)) return;

        int status = cursor.getInt(cursor.getColumnIndex(DataMessage$Table.STATUS));
        String id = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        // not outgoing and unread
        if (status == MessageStatus.SENT && !id.equals(user.getId())) {
            DataMessage m = SqlUtils.convertToModel(true, DataMessage.class, cursor);
            handler.postDelayed(() -> sendAndMarkChatEntities(m), delay);
        }
    }

    private void sendAndMarkChatEntities(DataMessage firstMessage) {
        if (!isConnectionPresent()) return;

        chatObservable.first()
                .flatMap(chat -> chat.sendReadStatus(firstMessage.getId())
                        .flatMap(msgId -> markMessagesAsRead(firstMessage)))
                .compose(new IoToMainComposer<>())
                .doOnNext(m -> Timber.i("Message marked as read %s", m))
                        //// TODO: 1/20/16 it's temporary crutch, that must be replaced with refactoring logic of invoking this method and using rxjava instead of handler
                .subscribe(message -> {
                }, throwable -> {
                    Timber.e(throwable, "Error while marking message as read");
                });
    }

    private Observable<DataMessage> markMessagesAsRead(DataMessage firstIncomingMessage) {
        //message does not contain toId
        return messageDAO
                .markMessagesAsRead(conversationId, user.getId(), firstIncomingMessage.getDate().getTime())
                .map(integer -> firstIncomingMessage)
                .first();
    }

    @Override
    public void onUnreadMessagesHeaderClicked() {
        messageDAO.countFromFirstUnreadMessage(conversationId, user.getId())
                .first()
                .filter(result -> result > 0)
                .compose(new IoToMainComposer<>())
                .subscribe(result -> {
                    int total = getView().getTotalShowingMessageCount();
                    int firstUnreadMessagePosition = total - result;
                    getView().smoothScrollToPosition(firstUnreadMessagePosition < 0 ? 0 : firstUnreadMessagePosition);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // New message
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean sendMessage(String message) {
        if (TextUtils.getTrimmedLength(message) == 0) {
            Toast.makeText(getContext(), R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        MessageBody body = new MessageBody();
        body.setText(message);
        body.setLocaleName(Locale.getDefault().toString());

        submitOneChatAction(chat -> {
            chat.send(createMessage(body))
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        });
        return true;
    }

    public Message createMessage(MessageBody body) {
        return new Message.Builder()
                .messageBody(body)
                .fromId(user.getId())
                .conversationId(conversationId)
                .build();
    }

    @Override
    public void retrySendMessage(String messageId) {
        attachmentDAO.getAttachmentByMessageId(messageId)
                .first()
                .subscribe(attachment -> {
                    if (attachment.getUploadTaskId() == 0) retrySendTextMessage(messageId);
                    else retryUploadAttachment(messageId);
                });
    }

    private void retrySendTextMessage(String messageId) {
        submitOneChatAction(chat -> messageDAO.getMessage(messageId)
                .first()
                .map(DataMessage::toChatMessage)
                .flatMap(chat::send)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    ///////////////////////////////////////////////////////////////////////////
    // User
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void openUserProfile(DataUser user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public DataUser getUser() {
        return user;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getToolbarMenuRes() {
        return R.menu.chat;
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {
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
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Flow.get(getContext()).set(new ExistingChatPath(conversationId));
                return true;
            case R.id.action_settings:
                conversationObservable
                        .first()
                        .compose(bindViewIoToMainComposer())
                        .subscribe(conversation -> {
                            if (conversationHelper.isGroup(conversation)) {
                                Flow.get(getContext()).set(new GroupSettingsPath(conversationId));
                            } else {
                                Flow.get(getContext()).set(new SingleSettingsPath(conversationId));
                            }
                        });
                return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Photo picking
    ///////////////////////////////////////////////////////////////////////////

    private void connectToPhotoPicker() {
        photoPickerDelegate = new PhotoPickerDelegate();
        ((Injector) context.getApplicationContext()).inject(photoPickerDelegate);
        //
        photoPickerDelegate.register();
        photoPickerDelegate
                .watchChosenImages()
                .compose(new IoToMainComposer<>())
                .compose(bindView())
                .subscribe(this::onImagesPicked,
                        e -> Timber.e(e, "Error while image picking"));
    }

    @Override
    public void onImagesPicked(List<ChosenImage> photos) {
        Timber.d("onImagesPicked %s", photos);
        attachmentDelegate
                .prepareMessageWithAttachment(user.getId(), conversationId, photos.get(0).getFileThumbnail())
                .subscribe();
    }

    private void disconnectFromPhotoPicker() {
        photoPickerDelegate.unregister();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Photo uploading
    ///////////////////////////////////////////////////////////////////////////

    private void uploadAttachment(String filePath) {
        attachmentDelegate
                .prepareMessageWithAttachment(user.getId(), conversationId, filePath)
                .subscribe();

    }

    private void retryUploadAttachment(String messageId) {
        attachmentDAO.getAttachmentByMessageId(messageId)
                .first()
                .doOnNext(BaseProviderModel::delete)
                .subscribe(dataAttachment -> uploadAttachment(dataAttachment.getUrl()));
    }

    private void connectToPendingAttachments() {
        attachmentDAO
                .getPendingAttachments(conversationId)
                .flatMap(cursor -> {
                    List<DataAttachment> attachments = SqlUtils.convertToList(DataAttachment.class, cursor);
                    cursor.close();
                    return Observable.from(attachments);
                })
                .flatMap(attachmentDelegate::bindToPendingAttachment)
                .compose(new IoToMainComposer<>())
                .compose(bindView())
                .subscribe(this::onAttachmentUploaded, e -> Timber.e("Image uploading failed"));
    }

    private void onAttachmentUploaded(DataAttachment dataAttachment) {
        messageDAO.getMessage(dataAttachment.getMessageId())
                .first()
                .subscribe(message -> sendMessageWithAttachment(message.toChatMessage(),
                        AttachmentHolder.newImageAttachment(dataAttachment.getUrl())));
    }

    private void sendMessageWithAttachment(Message message, AttachmentHolder attachmentHolder) {
        message.setMessageBody(new MessageBody(Collections.singletonList(attachmentHolder)));
        submitOneChatAction(chat -> chat.send(message).subscribe());
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
        handler.post(screen::showContent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private void submitOneChatAction(Action1<Chat> action1) {
        chatObservable.first()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(action1);
    }

    private long timeSinceMessagesUiInitialized() {
        return System.currentTimeMillis() - messagesUiWasInitializedTimestamp;
    }

    private static class ChatChangeStateEvent {
        private final String userId;
        private final String conversationId;
        private final String state;

        private ChatChangeStateEvent(String userId, String conversationId, String state) {
            this.userId = userId;
            this.conversationId = conversationId;
            this.state = state;
        }
    }
}
