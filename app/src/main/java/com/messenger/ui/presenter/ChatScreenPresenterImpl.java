package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.delegate.AttachmentDelegate;
import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.MessageTranslationDelegate;
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
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.notification.MessengerNotificationFactory;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.storage.helper.AttachmentHelper;
import com.messenger.synchmechanism.ConnectionStatus;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.PhotoPickerDelegate;
import com.messenger.ui.model.AttachmentMenuItem;
import com.messenger.ui.util.AttachmentMenuProvider;
import com.messenger.ui.util.ChatContextualMenuProvider;
import com.messenger.ui.view.add_member.ExistingChatPath;
import com.messenger.ui.view.chat.ChatPath;
import com.messenger.ui.view.chat.ChatScreen;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.settings.GroupSettingsPath;
import com.messenger.ui.view.settings.SingleSettingsPath;
import com.messenger.ui.view.settings.TripSettingsPath;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.PickLocationDelegate;
import com.messenger.util.Utils;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
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

    private static final int MAX_MESSAGE_PER_PAGE = 50;
    //
    private static final int MARK_AS_READ_DELAY = 2000;
    private static final int START_TYPING_DELAY = 1000;
    private static final int STOP_TYPING_DELAY = 2000;

    private final GlobalEventEmitter messengerGlobalEmitter;

    @Inject
    SessionHolder<UserSession> userSessionHolder;
    @Inject
    DataUser user;
    @Inject
    MessageBodyCreator messageBodyCreator;
    @Inject
    Router router;
    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    NotificationDelegate notificationDelegate;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    OpenedConversationTracker openedConversationTracker;
    @Inject
    AttachmentDelegate attachmentDelegate;
    @Inject
    PaginationDelegate paginationDelegate;
    @Inject
    PhotoPickerDelegate photoPickerDelegate;
    @Inject
    AttachmentMenuProvider attachmentMenuProvider;

    protected ProfileCrosser profileCrosser;
    protected ConversationHelper conversationHelper;
    protected AttachmentHelper attachmentHelper;
   
    protected String conversationId;

    @Inject
    ConversationsDAO conversationDAO;
    @Inject
    UsersDAO usersDAO;
    @Inject
    MessageDAO messageDAO;
    @Inject
    AttachmentDAO attachmentDAO;
    @Inject
    TranslationsDAO translationsDAO;

    @Inject
    StartChatDelegate startChatDelegate;
    @Inject
    LocaleHelper localeHelper;
    @Inject
    MessageTranslationDelegate messageTranslationDelegate;
    @Inject
    PickLocationDelegate pickLocationDelegate;

    private int page = 0;
    private long before = 0;
    private long openScreenTime;

    private boolean unreadMessagesLoading = false;
    private boolean loading = false;
    private boolean haveMoreElements = true;

    private boolean needShowUnreadMessages;
    private boolean firstLoadedMessageMarked;
    private boolean typing;
    private boolean imageAttachmentClicked = false;

    private Subscription messageStreamSubscription;
    private Observable<Chat> chatObservable;
    private Observable<Pair<DataConversation, List<DataUser>>> conversationObservable;
    private PublishSubject<ChatChangeStateEvent> chatStateStream;
    private PublishSubject<Pair<Cursor, Integer>> lastVisibleItemStream = PublishSubject.create();

    private ChatContextualMenuProvider contextualMenuProvider;

    public ChatScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context);
        this.conversationId = conversationId;

        injector.inject(this);

        messengerGlobalEmitter = messengerServerFacade.getGlobalEventEmitter();
        paginationDelegate.setPageSize(MAX_MESSAGE_PER_PAGE);
        profileCrosser = new ProfileCrosser(context, routeCreator);
        conversationHelper = new ConversationHelper();
        attachmentHelper = new AttachmentHelper(attachmentDAO, messageDAO, usersDAO);
        contextualMenuProvider = new ChatContextualMenuProvider(context, usersDAO, translationsDAO);
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
        connectToUnreadCounterStream();
        connectToLastVisibleItemStream();
        connectToShareLocationsStream();
        submitOneChatAction(this::connectChatTypingStream);
        loadInitialData();

        connectToPhotoPicker();
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
        connectShowSendMessageAction();
    }

    @Override
    public void onDetachedFromWindow() {
        closeChat();
        super.onDetachedFromWindow();
        paginationDelegate.stopPaginate();
        disconnectFromPhotoPicker();
    }

    private void closeChat() {
        chatObservable.take(1).subscribeOn(Schedulers.io()).subscribe(Chat::close);
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

                    // TODO Feb 29, 2016 Implement it in more Rx way
                    if (connectionStatus != ConnectionStatus.CONNECTED) {
                        getView().removeAllTypingUsers();
                    }
                }, e -> Timber.w("Unable to connect connectivity status"));
    }

    private void connectConversationStream() {
        ConnectableObservable<Pair<DataConversation, List<DataUser>>> source = conversationDAO.getConversationWithParticipants(conversationId)
                .filter(conversationWithParticipant -> conversationWithParticipant != null)
                .filter(conversationWithParticipant -> {
                    if (TextUtils.equals(conversationWithParticipant.first.getStatus(), ConversationStatus.PRESENT)) {
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

        source.compose(new NonNullFilter<>())
                .take(1)
                .subscribe(conversationUsersPair -> {
                    if (ConversationHelper.isSingleChat(conversationUsersPair.first))
                        TrackingHelper.openSingleConversation();
                    else
                        TrackingHelper.openGroupConversation(conversationUsersPair.second.size());
                }, throwable -> Timber.e(throwable, ""));

        source.doOnSubscribe(() -> getView().showLoading());

        source.compose(bindViewIoToMainComposer())
                .subscribe(conversationWithParticipants ->
                        conversationLoaded(conversationWithParticipants.first, conversationWithParticipants.second));

        source.connect();
    }

    private void connectToChatStream() {
        chatObservable = conversationObservable
                .take(1)
                .flatMap(conversationListWithParticipants ->
                        createChat(messengerServerFacade.getChatManager(), conversationListWithParticipants.first, conversationListWithParticipants.second)
                                .subscribeOn(Schedulers.io()))
                .replay(1)
                .autoConnect();
    }

    private void connectToUnreadCounterStream() {
        conversationObservable
                .map(conversationWithParticipants -> conversationWithParticipants.first.getUnreadMessageCount())
                .subscribe(count -> {
                    if (count == 0 && needShowUnreadMessages) {
                        needShowUnreadMessages = false;
                        getView().setShowMarkUnreadMessage(false);
                    }
                }, e -> Timber.w("Unable to connect to unread counter"));
    }

    private void connectChatTypingStream(Chat chat) {
        final OnChatStateChangedListener listener = (conversationId, userId, state) -> {
            chatStateStream.onNext(new ChatChangeStateEvent(userId, conversationId, state));
        };
        messengerGlobalEmitter.addOnChatStateChangedListener(listener);

        chatStateStream.asObservable()
                .onBackpressureBuffer()
                .filter(chatChangeStateEvent -> TextUtils.equals(chatChangeStateEvent.conversationId, conversationId))
                .compose(bindViewIoToMainComposer())
                .doOnUnsubscribe(() -> messengerGlobalEmitter.removeOnChatStateChangedListener(listener))
                .map(stateEvent -> new Pair<>(stateEvent, usersDAO.getUserById(stateEvent.userId).toBlocking().first()))
                .filter(chatChangeStateEventDataUserPair -> chatChangeStateEventDataUserPair.second != null)
                .subscribe(pair -> {
                            switch (pair.first.state) {
                                case ChatState.COMPOSING:
                                    getView().addTypingUser(pair.second);
                                    break;
                                case ChatState.PAUSE:
                                    getView().removeTypingUser(pair.second);
                                    break;
                            }
                        },
                        e -> Timber.w("Unable to connect chat stream"));
    }

    private void conversationLoaded(DataConversation conversation, List<DataUser> participants) {
        //noinspection all
        getView().setTitle(conversation, participants);
    }

    private void loadInitialData() {
        conversationObservable
                .take(1)
                .map(conversationWithParticipants -> conversationWithParticipants.first)
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

        if (syncTime != 0) needShowUnreadMessages = true;

        getView().setShowMarkUnreadMessage(true);

        Observable<Cursor> messagesObservable = messageDAO
                .getMessagesBySyncTime(conversationId, syncTime)
                .filter(cursor -> cursor.getCount() > 0);

        return Observable.combineLatest(messagesObservable, conversationObservable,
                (cursor, dataConversationListPair) -> new Pair<>(cursor, dataConversationListPair.first))
                .compose(bindViewIoToMainComposer())
                .subscribe(cursorAndConversation -> {
                    Cursor cursor = cursorAndConversation.first;
                    Timber.i("Retrived message count " + cursor.getCount());
                    if (!unreadMessagesLoading) {
                        markUnreadMessageFromDB(syncTime);
                    }

                    getView().showMessages(cursor, cursorAndConversation.second);
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
                }, e -> Timber.w("Failed to mark message as read"));
    }

    private void connectUnreadMessageCountStream(DataConversation conversation) {
        messageDAO.unreadCount(conversationId, user.getId())
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
                .onBackpressureLatest()
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

        conversationObservable.take(1)
                .compose(bindViewIoToMainComposer())
                .map(conversationWithParticipants -> conversationWithParticipants.first)
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
                .take(1)
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
                .filter(textViewTextChangeEvent -> textViewTextChangeEvent.count() > 0
                        && currentConnectivityStatus == ConnectionStatus.CONNECTED)
                .compose(bindVisibility())
                .throttleFirst(START_TYPING_DELAY, TimeUnit.MILLISECONDS)
                .filter(textViewTextChangeEvent -> !typing)
                .flatMap(textViewTextChangeEvent -> chatObservable.take(1))
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
                .flatMap(textViewTextChangeEvent -> chatObservable.take(1))
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

        chatObservable.take(1)
                .flatMap(chat -> chat.sendReadStatus(message.getId()).flatMap(this::markMessagesAsRead))
                .doOnNext(m -> Timber.i("Message marked as read %s", m))
                .flatMap(msg -> conversationObservable.take(1))
                .map(conversationWithParticipants -> conversationWithParticipants.first)
                .subscribe(dataConversation -> {
                    int unreadMessageCount = dataConversation.getUnreadMessageCount() - 1;
                    dataConversation.setUnreadMessageCount(unreadMessageCount < 0 ? 0 : unreadMessageCount);
                    conversationDAO.save(Collections.singletonList(dataConversation));
                }, throwable -> {
                    Timber.e(throwable, "Error while marking message as read");
                });
    }

    private Observable<Integer> markMessagesAsRead(String sinceMessageId) {
        //message does not contain toId
        return messageDAO
                .getMessage(sinceMessageId)
                .first()
                .flatMap(dataMessage -> messageDAO.markMessagesAsRead(conversationId, user.getId(), dataMessage.getDate().getTime()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Message process
    ///////////////////////////////////////////////////////////////////////////

    private void connectShowSendMessageAction() {
        getView().getEditMessageObservable()
                .compose(bindVisibility())
                .subscribe(event -> {
                    getView().enableSendMessageButton(TextUtils.getTrimmedLength(event.text()) > 0);
                });
    }

    @Override
    public boolean sendMessage(String message) {
        if (TextUtils.isEmpty(message)) return false;
        String finalMessage = message.trim();

        if (TextUtils.isEmpty(finalMessage)) return false;

        submitOneChatAction(chat -> chat.send(new Message.Builder()
                .messageBody(messageBodyCreator.provideForText(finalMessage))
                .fromId(user.getId())
                .conversationId(conversationId)
                .build())
                .subscribeOn(Schedulers.io())
                .subscribe());
        return true;
    }

    @Override
    public void retrySendMessage(DataMessage message) {
        attachmentDAO.getAttachmentByMessageId(message.getId())
                .first()
                .compose(bindView())
                .subscribe(attachment -> {
                    if (attachment != null) retrySendAttachment(message, attachment);
                    else retrySendTextMessage(message);
                });
    }

    private void retrySendTextMessage(DataMessage dataMessasge) {
        submitOneChatAction(chat -> {
                        Message message = dataMessasge.toChatMessage();
                        message.setMessageBody(messageBodyCreator.provideForText(dataMessasge.getText()));
                        chat.send(message).subscribe();
        });
    }

    @Override
    public void onCopyMessageTextToClipboard(DataMessage message) {
        translationsDAO.getTranslation(message.getId()).first()
                .map(translation -> {
                    if (translation != null && translation.getTranslateStatus() == TranslationStatus.TRANSLATED) {
                        return translation.getTranslation();
                    } else {
                        return message.getText();
                    }
                }).subscribe(text -> Utils.copyToClipboard(context, text));
    }

    @Override
    public void onTranslateMessage(DataMessage message) {
        messageTranslationDelegate.translateMessage(message, userSessionHolder);
    }

    @Override
    public void onRevertTranslate(DataMessage message) {
        messageTranslationDelegate.revertTranslation(message);
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
                .take(1)
                .compose(bindVisibilityIoToMainComposer())
                .map(conversationWithParticipants -> conversationWithParticipants.first)
                .subscribe(conversation -> {
                    if (ConversationHelper.isGroup(conversation)) {
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
                TrackingHelper.addPeopleToChat();
                Flow.get(getContext()).set(new ExistingChatPath(conversationId));
                return true;
            case R.id.action_settings:
                conversationObservable
                        .map(dataConversationStringPair -> dataConversationStringPair.first)
                        .take(1)
                        .compose(bindViewIoToMainComposer())
                        .subscribe(conversation -> {
                            if (ConversationHelper.isTripChat(conversation)) {
                                Flow.get(getContext()).set(new TripSettingsPath(conversationId));
                            } else if (ConversationHelper.isGroup(conversation)) {
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
        contextualMenuProvider
                .provideMenu(message, user, conversationObservable.map(dataConversationStringPair -> dataConversationStringPair.first),
                        attachmentDAO.getAttachmentByMessageId(message.getId()))
                .filter(menu -> menu.size() > 0)
                .compose(bindViewIoToMainComposer())
                .subscribe(menu -> getView().showContextualAction(menu, message));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Attachment menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onAttachmentButtonClick() {
        getView().showAttachmentMenu(attachmentMenuProvider.provide());
    }

    @Override
    public void onAttachmentMenuItemChosen(AttachmentMenuItem attachmentMenuItem) {
        switch (attachmentMenuItem.getType()) {
            case AttachmentMenuItem.LOCATION:
                pickLocationDelegate.pickLocation();
                break;
            case AttachmentMenuItem.IMAGE:
                getView().showPhotoPicker();
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Location picking
    ///////////////////////////////////////////////////////////////////////////

    private void connectToShareLocationsStream() {
        pickLocationDelegate
                .getPickedLocationsStream()
                .compose(bindView())
                .subscribe(notification -> {
                    if (notification.isOnNext()) {
                        onLocationPicked(notification.getValue());
                    } else if (notification.isOnError()) {
                        getView().showPickLocationError();
                    }
                });
    }

    private void onLocationPicked(Location location) {
        Timber.d(getClass().getSimpleName() + "::onLocationPicked %s", location);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Photo picking
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onImageClicked(String attachmentImageId) {
        if (imageAttachmentClicked) return;
        else imageAttachmentClicked = true;

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

                    imageAttachmentClicked = false;
                });
    }

    private void connectToPhotoPicker() {
        photoPickerDelegate.register();
        photoPickerDelegate
                .watchChosenImages()
                .compose(bindViewIoToMainComposer())
                .subscribe(photos -> {
                            getView().hidePhotoPicker();
                            onImagesPicked(photos);
                        },
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
                .subscribeOn(Schedulers.io())
                .subscribe(this::uploadAttachment, throwable -> Timber.e(throwable, ""));
    }

    private void uploadAttachment(String filePath) {
        conversationObservable.take(1)
                .subscribe(pair -> attachmentDelegate.send(pair.first, filePath),
                        throwable ->Timber.d(throwable, ""));
    }

    private void disconnectFromPhotoPicker() {
        photoPickerDelegate.unregister();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Photo uploading
    ///////////////////////////////////////////////////////////////////////////

    private void retrySendAttachment(DataMessage message, DataAttachment dataAttachment) {
        conversationObservable.take(1)
                .subscribe(pair -> attachmentDelegate.retry(pair.first, message, dataAttachment),
                        throwable -> Timber.e(throwable, ""));
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

        ChatScreen chatScreen = getView();
        ChatLayoutViewState viewState = getViewState();
        switch (viewState.getLoadingState()) {
            case LOADING:
                chatScreen.showLoading();
                break;
            case CONTENT:
                chatScreen.showContent();
                break;
            case ERROR:
                chatScreen.showError(viewState.getError());
                break;
        }
    }

    private void showContent() {
        submitActionToUi(o -> {
            ChatScreen chatScreen = getView();
            if (chatScreen != null) chatScreen.showContent();
        }, 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private Observable<Chat> createChat(ChatManager chatManager, DataConversation conversation, @NonNull List<DataUser> particioants) {
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                return Observable.just(particioants)
                        .filter(usersList -> !usersList.isEmpty())
                        .map(users -> users.get(0))
                        .map(mate -> chatManager.createSingleUserChat(mate.getId(), conversation.getId()));
            case ConversationType.GROUP:
            default:
                boolean isOwner = ConversationHelper.isOwner(conversation, user);
                return Observable.defer(() -> Observable.just(chatManager.createMultiUserChat(conversation.getId(), user.getId(), isOwner)));
        }
    }

    private void submitOneChatAction(Action1<Chat> action1) {
        chatObservable.take(1)
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
