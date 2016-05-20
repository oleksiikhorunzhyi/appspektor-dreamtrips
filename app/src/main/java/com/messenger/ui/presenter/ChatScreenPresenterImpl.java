package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.analytics.ConversationAnalyticsDelegate;
import com.messenger.delegate.AttachmentManager;
import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.MessageTranslationDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.delegate.chat.ChatDelegate;
import com.messenger.delegate.chat.ChatDelegate.PaginationStatus;
import com.messenger.delegate.chat.ChatTypingDelegate;
import com.messenger.delegate.chat.typing.ChatStateDelegate;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.TranslationStatus;
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
import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.adapter.inflater.LiteMapInflater;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.LegacyPhotoPickerDelegate;
import com.messenger.ui.model.AttachmentMenuItem;
import com.messenger.ui.module.flagging.FlaggingPresenter;
import com.messenger.ui.module.flagging.FlaggingPresenterImpl;
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
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionGrantedComposer;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;
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

import flow.Flow;
import flow.History;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class ChatScreenPresenterImpl extends MessengerPresenterImpl<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {
    //
    private static final int MARK_AS_READ_DELAY = 2000;

    @Inject MessageBodyCreator messageBodyCreator;
    @Inject DataUser user;
    @Inject SessionHolder<UserSession> sessionHolder;
    @Inject NotificationDelegate notificationDelegate;
    @Inject OpenedConversationTracker openedConversationTracker;
    @Inject AttachmentManager attachmentManager;
    @Inject LegacyPhotoPickerDelegate legacyPhotoPickerDelegate;
    @Inject AttachmentMenuProvider attachmentMenuProvider;
    @Inject StartChatDelegate startChatDelegate;
    @Inject MessageTranslationDelegate messageTranslationDelegate;
    @Inject PickLocationDelegate pickLocationDelegate;
    @Inject ChatDelegate chatDelegate;
    @Inject ChatContextualMenuProvider contextualMenuProvider;
    @Inject Router router;
    @Inject ProfileCrosser profileCrosser;
    @Inject ChatTypingDelegate chatTypingDelegate;
    @Inject ConversationsDAO conversationDAO;
    @Inject UsersDAO usersDAO;
    @Inject MessageDAO messageDAO;
    @Inject AttachmentDAO attachmentDAO;
    @Inject PhotoDAO photoDAO;
    @Inject LocationDAO locationDAO;
    @Inject TranslationsDAO translationsDAO;
    @Inject AttachmentHelper attachmentHelper;
    @Inject PermissionDispatcher permissionDispatcher;
    @Inject ConversationAnalyticsDelegate conversationAnalyticsDelegate;
    @Inject ChatStateDelegate chatStateDelegate;

    private FlaggingPresenter flaggingPresenter;

    protected String conversationId;

    private long openScreenTime;

    private boolean imageAttachmentClicked = false;

    private Subscription messageStreamSubscription;
    private Observable<Pair<DataConversation, List<DataUser>>> conversationObservable;
    private PublishSubject<DataMessage> lastVisibleItemStream = PublishSubject.create();

    private Injector injector;

    public ChatScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context, injector);
        this.conversationId = conversationId;
        this.injector = injector;
        openScreenTime = System.currentTimeMillis();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.flaggingPresenter = new FlaggingPresenterImpl(getView().getFlaggingView(), injector);
        //
        connectConnectivityStatusStream();
        connectConversationStream();
        connectChatTypingStream();
        connectToLastVisibleItemStream();
        connectToShareLocationsStream();
        loadInitialData();
        bindChatDelegate();
        connectToPhotoPicker();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        boolean visible = visibility == View.VISIBLE;
        openedConversationTracker.conversationVisibilityChanged(conversationId, visible);
        if (visible) {
            connectChatStateDelegate();
            connectShowSendMessageAction();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        chatDelegate.closeChat();
        disconnectFromPhotoPicker();
        super.onDetachedFromWindow();
    }

    ///////////////////////////////////////////////////
    ////// Streams
    //////////////////////////////////////////////////

    private void bindChatDelegate() {
        chatDelegate.bind(connectionStatusStream, conversationObservable)
                .compose(bindViewIoToMainComposer())
                .subscribe(this::handlePaginationStatus);
    }

    private void connectConnectivityStatusStream() {
        connectionStatusStream
                .subscribe(connectionStatus -> {
                    if (messageStreamSubscription != null && !messageStreamSubscription.isUnsubscribed()) {
                        messageStreamSubscription.unsubscribe();
                    }

                    long syncTime = connectionStatus == SyncStatus.CONNECTED ? openScreenTime : 0;
                    messageStreamSubscription = connectMessagesStream(syncTime);

                    if (connectionStatus == SyncStatus.CONNECTED) {
                        chatDelegate.loadFirstPage();
                    } else {
                        // TODO Feb 29, 2016 Implement it in more Rx way
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
                .subscribe(conversationUsersPair -> trackOpenedConversation(conversationUsersPair.first, conversationUsersPair.second),
                        throwable -> Timber.e(throwable, ""));

        source.doOnSubscribe(() -> getView().showLoading());

        source.compose(bindViewIoToMainComposer())
                .subscribe(conversationWithParticipants ->
                        conversationLoaded(conversationWithParticipants.first,
                                conversationWithParticipants.second));

        source.connect();
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
                if (paginationStatus.page == 1) view.setShowMarkUnreadMessage(true);
                view.showLoading();
                getViewState().setLoadingState(ChatLayoutViewState.LoadingState.LOADING);
                break;
            case SUCCESS:
                if (paginationStatus.page == 1 && paginationStatus.loadedElementsCount == 0) {
                    view.setShowMarkUnreadMessage(false);
                }
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
    ////// Chat typing state logic
    //////////////////////////////////////////////////////////////////////////

    private void connectChatStateDelegate() {
        chatStateDelegate.init(conversationId);
        chatStateDelegate.connectTypingStartAction(getView().getEditMessageObservable()
                .map(TextViewTextChangeEvent::text))
                .compose(bindVisibility())
                .subscribe();
        chatStateDelegate.connectTypingStopAction(getView().getEditMessageObservable()
                .map(TextViewTextChangeEvent::text))
                .compose(bindVisibility())
                .subscribe();
    }

    private void connectChatTypingStream() {
        chatTypingDelegate
                .connectChatTypingStream(conversationId)
                .compose(bindViewIoToMainComposer())
                .subscribe(pair -> {
                    switch (pair.first.state) {
                        case ChatState.COMPOSING:
                            getView().addTypingUser(pair.second);
                            break;
                        case ChatState.PAUSE:
                            getView().removeTypingUser(pair.second);
                            break;
                    }
                }, e -> Timber.w("Unable to connect chat stream"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unread and Mark as read
    ///////////////////////////////////////////////////////////////////////////

    private void tryMarkAsReadMessage(DataMessage lastMessage) {
        if (!isConnectionPresent()) return;

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
        String finalMessage = message.trim();

        if (TextUtils.isEmpty(finalMessage)) return false;

        chatDelegate.sendMessage(new Message.Builder()
                .messageBody(messageBodyCreator.provideForText(finalMessage))
                .fromId(user.getId())
                .conversationId(conversationId)
                .build());
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
        Message message = dataMessage.toChatMessage();
        message.setMessageBody(messageBodyCreator.provideForText(dataMessage.getText()));
        chatDelegate.sendMessage(message);
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
        messageTranslationDelegate.translateMessage(message, sessionHolder);
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
                .provideMenu(message, conversationObservable.map(dataConversationStringPair -> dataConversationStringPair.first),
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
                .requestPermission(PermissionConstants.STORE_PERMISSIONS, false)
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

        attachmentHelper.obtainPhotoAttachment(attachmentImageId, user)
                .compose(bindViewIoToMainComposer())
                .subscribe(photoAttachment -> {
                    ArrayList<IFullScreenObject> items = new ArrayList<>();
                    items.add(photoAttachment);
                    router.moveTo(Route.FULLSCREEN_PHOTO_LIST,
                            NavigationConfigBuilder.forActivity()
                                    .data(new FullScreenImagesBundle.Builder()
                                            .position(0)
                                            .type(TripImagesType.FIXED)
                                            .route(Route.MESSAGE_IMAGE_FULLSCREEN)
                                            .fixedList(items)
                                            .build())
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
    // Flagging
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onFlagMessageAttempt(DataMessage message) {
        flaggingPresenter.flagMessage(conversationId, message.getId());
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

    private void trackOpenedConversation(DataConversation openedConversation, List<DataUser> participants) {
        conversationAnalyticsDelegate.trackOpenedConversation(openedConversation, participants);
    }
}
