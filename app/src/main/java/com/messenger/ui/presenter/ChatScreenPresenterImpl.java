package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
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
import com.messenger.delegate.StartChatDelegate;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
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
import com.messenger.storage.helper.AttachmentHelper;
import com.messenger.storage.helper.ParticipantsDaoHelper;
import com.messenger.synchmechanism.ConnectionStatus;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.PhotoPickerDelegate;
import com.messenger.ui.util.ChatContextualMenuProvider;
import com.messenger.ui.view.add_member.ExistingChatPath;
import com.messenger.ui.view.chat.ChatPath;
import com.messenger.ui.view.chat.ChatScreen;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.settings.GroupSettingsPath;
import com.messenger.ui.view.settings.SingleSettingsPath;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.Utils;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow;
import flow.History;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class ChatScreenPresenterImpl extends MessengerPresenterImpl<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {

    private static final int MAX_MESSAGE_PER_PAGE = 20;
    //
    private static final int MARK_AS_READ_DELAY = 2000;
    private static final int START_TYPING_DELAY = 1000;
    private static final int STOP_TYPING_DELAY = 2000;

    private final GlobalEventEmitter messengerGlobalEmitter;

    @Inject
    DataUser user;
    @Inject
    Router router;
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
    protected ParticipantsDaoHelper participantsDaoHelper;
    protected AttachmentHelper attachmentHelper;
    protected PhotoPickerDelegate photoPickerDelegate;

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

    @Inject
    StartChatDelegate startChatDelegate;

    private int page = 0;
    private long before = 0;
    private long openScreenTime;

    private boolean unreadMessagesLoading = false;
    private boolean loading = false;
    private boolean haveMoreElements = true;

    private boolean needShowUnreadMessages;
    private boolean unreadCounterShown;
    private boolean firstLoadedMessageMarked;
    private boolean typing;

    private Subscription messageStreamSubscription;
    private Subscription participantsStreamSubcription;
    private Observable<Chat> chatObservable;
    private Observable<DataConversation> conversationObservable;
    private PublishSubject<ChatChangeStateEvent> chatStateStream;
    private PublishSubject<Pair<Cursor, Integer>> lastVisibleItemStream = PublishSubject.create();

    private ChatContextualMenuProvider contextualMenuInflater;

    public ChatScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context);
        this.conversationId = conversationId;

        injector.inject(this);

        messengerGlobalEmitter = messengerServerFacade.getGlobalEventEmitter();
        backStackDelegate.setListener(() -> !isViewAttached() || getView().onBackPressed());
        paginationDelegate = new PaginationDelegate(messengerServerFacade, messageDAO, attachmentDAO, MAX_MESSAGE_PER_PAGE);
        profileCrosser = new ProfileCrosser(context, routeCreator);
        conversationHelper = new ConversationHelper();
        participantsDaoHelper = new ParticipantsDaoHelper(participantsDAO);
        attachmentHelper = new AttachmentHelper(attachmentDAO, messageDAO, usersDAO);
        contextualMenuInflater = new ChatContextualMenuProvider(context);
        //
        chatStateStream = PublishSubject.<ChatChangeStateEvent>create();
        openScreenTime = System.currentTimeMillis();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        connectConnectivityStatusStream();
        connectConversationStream();
        connectToChatStream();
        connectMembersStream();
        connectToUnreadCounterStream();
        connectToLastVisibleItemStream();
        submitOneChatAction(this::connectChatTypingStream);
        loadInitialData();

        connectToPhotoPicker();
        connectToPendingAttachments();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            openedConversationTracker.addOpenedConversation(conversationId);
        } else {
            openedConversationTracker.removeOpenedConversation(conversationId);
            return;
        }

        connectTypingStartAction();
        connectTypingStopAction();
    }

    @Override
    public void onDetachedFromWindow() {
        closeChat();
        super.onDetachedFromWindow();
        paginationDelegate.stopPaginate();
        backStackDelegate.setListener(null);
        disconnectFromPhotoPicker();

    }

    private void closeChat() {
        chatObservable.first().subscribeOn(Schedulers.io()).subscribe(Chat::close);
    }
    ///////////////////////////////////////////////////
    ////// Streams
    //////////////////////////////////////////////////

    private void connectConnectivityStatusStream() {
        connectionStatusStream
                .subscribe(connectionStatus -> {
                    if (messageStreamSubscription != null && !messageStreamSubscription.isUnsubscribed()) {
                        messageStreamSubscription.unsubscribe();
                    }

                    if (page == 0 && connectionStatus == ConnectionStatus.CONNECTED) {
                        startLoadHistory();
                    }

                    long syncTime = connectionStatus == ConnectionStatus.CONNECTED ? openScreenTime : 0;
                    messageStreamSubscription = connectMessagesStream(syncTime);
                }, e -> Timber.w("Unable to connect connectivity status"));
    }

    private void connectConversationStream() {
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

        source.doOnSubscribe(() -> getView().showLoading());
        source.connect();
    }

    private void connectToChatStream() {
        chatObservable = conversationObservable
                .first()
                .flatMap(c -> createChat(messengerServerFacade.getChatManager(), c)
                        .subscribeOn(Schedulers.io()))
                .replay(1)
                .autoConnect();
    }

    protected void connectMembersStream() {
        conversationObservable.subscribe(dataConversation -> {
            if (participantsStreamSubcription != null && !participantsStreamSubcription.isUnsubscribed()) {
                participantsStreamSubcription.unsubscribe();
            }

            participantsStreamSubcription = participantsDaoHelper.obtainParticipantsStream(dataConversation, user)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(dataUsers -> getView().setTitle(dataConversation, dataUsers));
        }, e -> Timber.w("Unable to connectMembersStream"));
    }

    private void connectToUnreadCounterStream() {
        conversationObservable.map(c -> c.getUnreadMessageCount())
                .subscribe(count -> {
                    ChatScreen screen = getView();
                    // we should show unread message counter one time per one connection
                    if (count == 0 && (unreadCounterShown || needShowUnreadMessages)) {
                        needShowUnreadMessages = false;
                        screen.setShowMarkUnreadMessage(false);
                        screen.hideUnreadMessageCount();
                    }
                    if (count != 0 && needShowUnreadMessages) {
                        unreadCounterShown = true;
                        screen.showUnreadMessageCount(count);
                    }
                }, e -> Timber.w("Unable to connect to unread counter"));
    }

    private void connectChatTypingStream(Chat chat) {
        final OnChatStateChangedListener listener = (conversationId, userId, state) -> {
            chatStateStream.onNext(new ChatChangeStateEvent(userId, conversationId, state));
        };
        messengerGlobalEmitter.addOnChatStateChangedListener(listener);

        chatStateStream.asObservable()
                .filter(chatChangeStateEvent -> TextUtils.equals(chatChangeStateEvent.conversationId, conversationId))
                .compose(bindViewIoToMainComposer())
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
                                }, e -> Timber.w("Unable to get user")),
                        e -> Timber.w("Unable to connect chat stream"));
    }

    private void loadInitialData() {
        conversationObservable
                .first()
                .compose(new IoToMainComposer<>())
                .subscribe(conversation -> {
                    notificationDelegate.cancel(MessengerNotificationFactory.MESSENGER_TAG);
                    //
                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
                    connectUnreadMessageCountStream(conversation);
                }, e -> Timber.w("Unable to load initial Data"));
    }

    private Subscription connectMessagesStream(long syncTime) {
        firstLoadedMessageMarked = false;
        // we must show new unread messages for new connection
        if (syncTime != 0) {
            needShowUnreadMessages = true;
            unreadCounterShown = false;
        }
        getView().setShowMarkUnreadMessage(true);

        return messageDAO.getMessagesBySyncTime(conversationId, syncTime)
                .onBackpressureLatest()
                .filter(cursor -> cursor.getCount() > 0)
                .compose(bindViewIoToMainComposer())
                .subscribe(cursor -> {
                    if (!unreadMessagesLoading) {
                        markUnreadMessageFromDB(syncTime);
                    }

                    conversationObservable.first()
                            .compose(bindViewIoToMainComposer())
                            .subscribe(conversation -> {
                                getView().showMessages(cursor, conversation);
                            }, e -> Timber.w("Unable to get conversation"));
                }, e -> Timber.w("Unable to get messages"));
    }

    private void markUnreadMessageFromDB(long syncTime) {
        Observable<DataMessage> observable = messageDAO.findNewestUnreadMessage(conversationId, user.getId(), syncTime).first();
        // we should mark unread message with 2 sec delay for showing unread message counter for 2 seconds
        // otherwise we shouldn't.
        if (needShowUnreadMessages && !firstLoadedMessageMarked) {
            observable = observable.throttleWithTimeout(MARK_AS_READ_DELAY, TimeUnit.MILLISECONDS);
        }
        observable
                .compose(new NonNullFilter<>())
                .compose(bindVisibilityIoToMainComposer())
                .subscribe(dataMessage -> {
                    firstLoadedMessageMarked = true;
                    tryMarkAsReadMessage(dataMessage);
                });
    }

    private void connectUnreadMessageCountStream(DataConversation conversation) {
        messageDAO.unreadCount(conversationId, user.getId())
                .onBackpressureLatest()
                .compose(bindVisibilityIoToMainComposer())
                .doOnNext(unreadCount -> {
                    if (conversation.getUnreadMessageCount() != unreadCount) {
                        conversation.setUnreadMessageCount(unreadCount);
                        conversationDAO.save(conversation);
                    }
                })
                .subscribe();
    }

    private void connectToLastVisibleItemStream() {
        lastVisibleItemStream.throttleLast(MARK_AS_READ_DELAY, TimeUnit.MILLISECONDS)
                .compose(bindViewIoToMainComposer())
                .subscribe(cursorIntegerPair -> {
                    Cursor cursor = cursorIntegerPair.first;
                    int position = cursorIntegerPair.second;

                    if (!unreadMessagesLoading && cursor != null && !cursor.isClosed()) {
                        int prevPos = cursor.getPosition();
                        cursor.moveToPosition(position);
                        DataMessage message = SqlUtils.convertToModel(true, DataMessage.class, cursor);
                        cursor.moveToPosition(prevPos);

                        tryMarkAsReadMessage(message);
                    }
                }, e -> Timber.w("Unable to submitOneChatAction"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Message Pagination
    ///////////////////////////////////////////////////////////////////////////

    private void startLoadHistory() {
        unreadMessagesLoading = true;
        loadNextPage();
    }

    private void loadNextPage() {
        if (!haveMoreElements || loading) return;

        loading = true;
        getView().showLoading();
        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.LOADING);

        conversationObservable.first()
                .compose(bindViewIoToMainComposer())
                .subscribe(conversation -> {
                    paginationDelegate.loadConversationHistoryPage(conversation, ++page, before,
                            (loadedPage, loadedMessage) ->
                                    submitActionToUi(o -> paginationPageLoaded(loadedMessage), 0),
                            () ->
                                    submitActionToUi(o -> {
                                        page--;
                                        showContent();
                                    }, 0)
                    );
                }, e -> Timber.w("Unable to get conversation"));
    }

    private void paginationPageLoaded(List<Message> loadedMessages) {
        if (getView() == null) return;

        loading = false;
        // pagination stops when we loaded nothing. In otherwise we can load not whole page cause localeName is present in some messages
        if (loadedMessages == null || loadedMessages.size() == 0) {
            haveMoreElements = false;
            unreadMessagesLoading = false;
            showContent();
            return;
        }

        int loadedCount = loadedMessages.size();
        Message lastMessage = loadedMessages.get(loadedCount - 1);
        before = lastMessage.getDate();

        if (unreadMessagesLoading) {
            if (!isLastLoadedMessageRead(loadedMessages)) {
                loadNextPage();
                return;
            } else {
                unreadMessagesLoading = false;
            }
        }

        showContent();
    }

    @Override
    public void onLastVisibleMessageChanged(Cursor cursor, int position) {
        lastVisibleItemStream.onNext(new Pair<>(cursor, position));
    }

    @Override
    public void onNextPageReached() {
        conversationObservable
                .first()
                .filter(conversation -> !loading)
                .compose(bindViewIoToMainComposer())
                .subscribe(conversation -> loadNextPage(),
                        e -> Timber.w("Unable to reach next page"));
    }

    ///////////////////////////////////////////////////////////////////////////
    ////// Typing logic
    //////////////////////////////////////////////////////////////////////////

    private void connectTypingStartAction() {
        getView().getEditMessageObservable()
                .skip(1)
                .filter(textViewTextChangeEvent -> textViewTextChangeEvent.count() > 0)
                .compose(bindVisibility())
                .throttleFirst(START_TYPING_DELAY, TimeUnit.MILLISECONDS)
                .filter(textViewTextChangeEvent -> !typing)
                .flatMap(textViewTextChangeEvent -> chatObservable.first())
                .subscribe(chat -> {
                    typing = true;
                    chat.setCurrentState(ChatState.COMPOSING);
                }, e -> Timber.w("Unable to connect to Typing start"));
    }


    private void connectTypingStopAction() {
        getView().getEditMessageObservable()
                .compose(bindVisibility())
                .skip(1)
                .debounce(STOP_TYPING_DELAY, TimeUnit.MILLISECONDS)
                .flatMap(textViewTextChangeEvent -> chatObservable.first())
                .subscribe(chat -> {
                    typing = false;
                    chat.setCurrentState(ChatState.PAUSE);
                }, e -> Timber.w("Unable to connect to Typing stop"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unread and Mark as read
    ///////////////////////////////////////////////////////////////////////////

    private void tryMarkAsReadMessage(DataMessage message) {
        if (!isConnectionPresent() || message.getStatus() == MessageStatus.READ
                || message.getFromId().equals(user.getId())) {
            return;
        }

        chatObservable.first()
                .flatMap(chat -> chat.sendReadStatus(message.getId()).flatMap(this::markMessagesAsRead))
                .compose(new IoToMainComposer<>())
                .doOnNext(m -> Timber.i("Message marked as read %s", m))
                .subscribe(msg -> {
                    conversationObservable.first().subscribe(dataConversation -> {
                        int unreadMessageCount = dataConversation.getUnreadMessageCount() - 1;
                        dataConversation.setUnreadMessageCount(unreadMessageCount < 0 ? 0 : unreadMessageCount);
                        conversationDAO.save(Collections.singletonList(dataConversation));
                    });
                }, throwable -> {
                    Timber.e(throwable, "Error while marking message as read");
                });
    }

    private Observable<Integer> markMessagesAsRead(String sinceMessageId) {
        //message does not contain toId
        return messageDAO
                .getMessage(sinceMessageId)
                .flatMap(dataMessage -> messageDAO.markMessagesAsRead(conversationId, user.getId(), dataMessage.getDate().getTime()))
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
    // Message process
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean sendMessage(String message) {
        if (TextUtils.getTrimmedLength(message) == 0) {
            Toast.makeText(getContext(), R.string.chat_message_toast_empty_message_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        MessageBody body = new MessageBody(message);

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
                    if (attachment != null) {
                        if (attachment.getUploadTaskId() != 0) retryUploadAttachment(messageId);
                        else retrySendAttachment(attachment);
                    } else retrySendTextMessage(messageId);
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

    @Override
    public void onCopyMessageTextToClipboard(DataMessage message) {
        Utils.copyToClipboard(context, message.getText());
    }

    @Override
    public void onStartNewChatForMessageOwner(DataMessage message) {
        usersDAO.getUserById(message.getFromId())
                .first()
                .compose(new IoToMainComposer<>())
                .subscribe(user -> {
                    Action1<DataConversation> action1 = conversation -> {
                        History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
                        history.pop();
                        history.push(new ChatPath(conversation.getId()));
                        Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
                    };
                    startChatDelegate.startSingleChat(user, action1);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // User
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DataUser getUser() {
        return user;
    }

    @Override
    public void openUserProfile(DataUser user) {
        profileCrosser.crossToProfile(user);
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
                }, e -> Timber.w("Unable to get last conversation"));

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
                }, e -> Timber.w("Unable to get conversation"));
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
    // Contextual Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onShowContextualMenu(DataMessage message) {
        contextualMenuInflater
                .provideMenu(message, user, conversationObservable,
                        attachmentDAO.getAttachmentByMessageId(message.getId()))
                .filter(menu -> menu.size() > 0)
                .subscribe(menu -> getView().showContextualAction(menu, message));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Photo picking
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onImageClicked(String attachmentImageId) {
        attachmentHelper.obtainPhotoAttachment(attachmentImageId)
                .compose(bindViewIoToMainComposer())
                .subscribe(photoAttachment -> {
                    ArrayList<IFullScreenObject> items = new ArrayList<>();
                    items.add(photoAttachment);
                    FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                            .position(0)
                            .type(TripImagesType.FIXED)
                            .route(Route.MESSAGE_IMAGE_FULLSCREEN)
                            .fixedList(items)
                            .build();

                    router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity()
                            .data(data)
                            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                            .build());
                });
    }

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
        if (photos == null || photos.isEmpty()) return;
        //
        Observable.from(photos)
                .first()
                .map(photo -> photos.get(0).getFileThumbnail())
                .map(filePath -> UploadingFileManager.copyFileIfNeed(filePath, context))
                .compose(new IoToMainComposer<>())
                .subscribe(this::uploadAttachment);
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

    private void retrySendAttachment(DataAttachment dataAttachment) {
        onAttachmentUploaded(dataAttachment);
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
        submitActionToUi(o -> {
            if (getView() != null) getView().showContent();
        }, 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private Observable<Chat> createChat(ChatManager chatManager, DataConversation conversation) {
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                return participantsDAO
                        .getParticipant(conversation.getId(), user.getId())
                        .first()
                        .compose(new NonNullFilter<>())
                        .map(mate -> chatManager.createSingleUserChat(mate.getId(), conversation.getId()));
            case ConversationType.GROUP:
            default:
                boolean isOwner = conversationHelper.isOwner(conversation, user);
                return Observable.defer(() -> Observable.just(chatManager.createMultiUserChat(conversation.getId(), user.getId(), isOwner)));
        }
    }

    private void submitOneChatAction(Action1<Chat> action1) {
        chatObservable.first()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(action1, e -> Timber.w("Unable to submitOneChatAction"));
    }

    private void submitActionToUi(Action1 action, int delay) {
        Observable observable = Observable.just(null);
        if (delay != 0) observable.throttleWithTimeout(delay, TimeUnit.MILLISECONDS);

        observable
                .compose(new IoToMainComposer<>())
                .subscribe(action, e -> Timber.w("Unable to submit action to UI"));
    }

    private boolean isLastLoadedMessageRead(List<Message> loadedMessages) {
        ListIterator<Message> iterator = loadedMessages.listIterator(loadedMessages.size());
        while (iterator.hasPrevious()) {
            Message message = iterator.previous();
            if (!TextUtils.equals(message.getFromId(), user.getId())) {
                return message.getStatus() == MessageStatus.READ;
            }
        }
        return true;
    }

    /////////////////////////////////////////////////////
    /////// Helper Classes
    ////////////////////////////////////////////////////

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
