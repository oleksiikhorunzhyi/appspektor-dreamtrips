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

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.delegate.AttachmentManager;
import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.ChatDelegate.PaginationStatus;
import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.MessageTranslationDelegate;
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
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.notification.MessengerNotificationFactory;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.LocationDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.PhotoDAO;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.storage.helper.AttachmentHelper;
import com.messenger.synchmechanism.ConnectionStatus;
import com.messenger.ui.adapter.inflater.LiteMapInflater;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.LegacyPhotoPickerDelegate;
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
import com.messenger.util.ExternalMapLauncher;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.PickLocationDelegate;
import com.messenger.util.Utils;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionGrantedComposer;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.List;
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
    AttachmentManager attachmentManager;
    @Inject
    LegacyPhotoPickerDelegate legacyPhotoPickerDelegate;
    @Inject
    AttachmentMenuProvider attachmentMenuProvider;
    @Inject
    PermissionDispatcher permissionDispatcher;

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
    PhotoDAO photoDAO;
    @Inject
    LocationDAO locationDAO;
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

    @Inject
    ChatDelegate chatDelegate;

    private long openScreenTime;

    private boolean typing;
    private boolean imageAttachmentClicked = false;

    private Subscription messageStreamSubscription;
    private Observable<Chat> chatObservable;
    private Observable<Pair<DataConversation, List<DataUser>>> conversationObservable;
    private PublishSubject<ChatChangeStateEvent> chatStateStream;
    private PublishSubject<DataMessage> lastVisibleItemStream = PublishSubject.create();

    private ChatContextualMenuProvider contextualMenuProvider;

    public ChatScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context);
        this.conversationId = conversationId;

        injector.inject(this);

        messengerGlobalEmitter = messengerServerFacade.getGlobalEventEmitter();
        profileCrosser = new ProfileCrosser(context, routeCreator);
        conversationHelper = new ConversationHelper();
        attachmentHelper = new AttachmentHelper(photoDAO, messageDAO, usersDAO);
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
        connectToLastVisibleItemStream();
        connectToShareLocationsStream();
        submitOneChatAction(this::connectChatTypingStream);
        loadInitialData();
        bindChatDelegate();
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
        disconnectFromPhotoPicker();
    }

    private void closeChat() {
        chatObservable.take(1).subscribeOn(Schedulers.io()).subscribe(Chat::close);
    }
    ///////////////////////////////////////////////////
    ////// Streams
    //////////////////////////////////////////////////

    private void bindChatDelegate() {
        chatDelegate.bind(connectionStatusStream, chatObservable, conversationObservable.map(pair -> pair.first))
                .compose(bindViewIoToMainComposer())
                .subscribe(this::handlePaginationStatus);
    }

    private void connectConnectivityStatusStream() {
        connectionStatusStream
                .subscribe(connectionStatus -> {
                    if (messageStreamSubscription != null && !messageStreamSubscription.isUnsubscribed()) {
                        messageStreamSubscription.unsubscribe();
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
                    getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
                }, e -> Timber.w("Unable to load initial Data"));
    }

    private Subscription connectMessagesStream(long syncTime) {
        Observable<Cursor> messagesObservable = messageDAO
                .getMessagesBySyncTime(conversationId, syncTime)
                .filter(cursor -> cursor.getCount() > 0);

        return Observable.combineLatest(messagesObservable, conversationObservable,
                (cursor, dataConversationListPair) -> new Pair<>(cursor, dataConversationListPair.first))
                .compose(bindViewIoToMainComposer())
                .subscribe(cursorAndConversation -> {
                    Cursor cursor = cursorAndConversation.first;
                    getView().showMessages(cursor, cursorAndConversation.second);
                }, e -> Timber.w("Unable to get messages"));
    }

    private void connectToLastVisibleItemStream() {
        lastVisibleItemStream.throttleLast(MARK_AS_READ_DELAY, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .compose(bindViewIoToMainComposer())
                .subscribe(this::tryMarkAsReadMessage,
                        e -> Timber.w("Unable to submitOneChatAction"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Message Pagination
    ///////////////////////////////////////////////////////////////////////////

    private void handlePaginationStatus(PaginationStatus paginationStatus) {
        ChatScreen view = getView();
        switch (paginationStatus.status) {
            case START:
                if (paginationStatus.page == 0) view.setShowMarkUnreadMessage(true);
                view.showLoading();
                getViewState().setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
                break;
            default:
                view.showContent();
        }
    }

    @Override
    public void onLastVisibleMessageChanged(Cursor cursor, int position) {
        DataMessage message = cursor.isClosed() || !cursor.moveToPosition(position) ? null :
                messageDAO.fromCursor(cursor, false);
        if (message != null) lastVisibleItemStream.onNext(message);
    }

    @Override
    public void onNextPageReached() {
        chatDelegate.loadNextPage();
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

    private void tryMarkAsReadMessage(DataMessage lastMessage) {
        if (!isConnectionPresent()) {
            return;
        }
        getView().setShowMarkUnreadMessage(false);
        chatDelegate.tryMarkAsReadMessage(lastMessage);
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
                .subscribe(msg -> {
                }, throwable -> Timber.e("Error while sending massage")));
        return true;
    }

    @Override
    public void retrySendMessage(DataMessage message) {
        String messageId = message.getId();

        attachmentDAO.getAttachmentByMessageId(messageId).take(1)
                .compose(bindView())
                .subscribe(attachment -> {
                    if (attachment != null) retrySendAttachment(message, attachment);
                    else retrySendTextMessage(message);
                });
    }

    private void retrySendTextMessage(DataMessage dataMessage) {
        submitOneChatAction(chat -> {
            Message message = dataMessage.toChatMessage();
            message.setMessageBody(messageBodyCreator.provideForText(dataMessage.getText()));
            chat.send(message).subscribe(msg -> {
            }, throwable -> Timber.e(throwable, "Error while resending message"));
        });
    }

    private void retrySendAttachment(DataMessage message, DataAttachment dataAttachment) {
        obtainConversationObservable()
                .subscribe(conversation -> attachmentManager.retrySendAttachment(conversation, message, dataAttachment));
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
                //noinspection ConstantConditions
                getView().hidePhotoPicker();
                pickLocationDelegate.pickLocation();
                break;
            case AttachmentMenuItem.IMAGE:
                showPhotoPicker();
                break;
        }
    }

    private void showPhotoPicker() {
        //noinspection ConstantConditions
        permissionDispatcher
                .requestPermission(PermissionConstants.STORE_PERMISSIONS)
                .compose(new PermissionGrantedComposer())
                .compose(bindView())
                .subscribe(aVoid -> getView().showPhotoPicker());
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
        obtainConversationObservable()
                .subscribe(conversation -> attachmentManager.sendLocation(conversation, location));
    }

    @Override
    public void onMapClicked(LatLng latLng) {
        new ExternalMapLauncher(context)
                .setLocationWithMarker(latLng.latitude, latLng.longitude)
                .setZoomLevel(LiteMapInflater.ZOOM_LEVEL)
                .launch();
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
        legacyPhotoPickerDelegate.register();
        legacyPhotoPickerDelegate
                .watchChosenImages()
                .compose(bindViewIoToMainComposer())
                .subscribe(photos -> {
                    getView().hidePhotoPicker();
                    uploadPhotoAttachments(Queryable.from(photos)
                            .map(ChosenImage::getFilePathOriginal)
                            .toList());
                }, e -> Timber.e(e, "Error while image picking"));
    }

    @Override
    public void onImagesPicked(List<BasePhotoPickerModel> photos) {
        if (photos == null || photos.isEmpty()) return;
        //
        uploadPhotoAttachments(Queryable.from(photos)
                .map(BasePhotoPickerModel::getOriginalPath)
                .toList());
    }

    private void uploadPhotoAttachments(List<String> filePaths) {
        conversationObservable.take(1)
                .subscribe(pair -> attachmentManager.sendImages(pair.first, filePaths),
                        throwable -> Timber.d(throwable, ""));
    }

    private void disconnectFromPhotoPicker() {
        legacyPhotoPickerDelegate.unregister();
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

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////
    private Observable<DataConversation> obtainConversationObservable() {
        return conversationObservable
                .take(1)
                .map(dataConversationListPair -> dataConversationListPair.first);
    }

    // TODO: 4/13/16 may be create `CreateChatHelper` ?
    private Observable<Chat> createChat(ChatManager chatManager, DataConversation conversation, @NonNull List<DataUser> participants) {
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                return Observable.just(participants)
                        .filter(usersList -> !usersList.isEmpty())
                        .map(users -> users.get(0))
                        .map(mate -> chatManager.createSingleUserChat(mate.getId(), conversation.getId()));
            case ConversationType.GROUP:
            default:
                boolean isOwner = ConversationHelper.isOwner(conversation, user);
                return Observable.defer(() -> Observable.just(chatManager.createMultiUserChat(conversation.getId(), user.getId(), isOwner)));
        }
    }

    private void submitOneChatAction(Action1<Chat> action) {
        chatObservable.take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(action, e -> Timber.w("Unable to submitOneChatAction"));
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
