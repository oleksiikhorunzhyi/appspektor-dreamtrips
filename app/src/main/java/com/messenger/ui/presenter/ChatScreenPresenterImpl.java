package com.messenger.ui.presenter;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.messenger.analytics.ConversationAnalyticsDelegate;
import com.messenger.delegate.MessageTranslationDelegate;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.delegate.chat.ChatDelegate;
import com.messenger.delegate.chat.ChatDelegate.PaginationStatus;
import com.messenger.delegate.chat.ChatTypingDelegate;
import com.messenger.delegate.chat.attachment.ChatMessageManager;
import com.messenger.delegate.chat.typing.ChatStateDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.notification.MessengerNotificationFactory;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.adapter.inflater.LiteMapInflater;
import com.messenger.ui.helper.ChatUserInteractionHelper;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.model.AttachmentMenuItem;
import com.messenger.ui.module.flagging.FlaggingPresenter;
import com.messenger.ui.util.AttachmentMenuProvider;
import com.messenger.ui.util.ChatContextualMenuProvider;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegate;
import com.messenger.ui.util.menu.ChatToolbarMenuProvider;
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
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.Subscription;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import timber.log.Timber;

public class ChatScreenPresenterImpl extends MessengerPresenterImpl<ChatScreen, ChatLayoutViewState>
        implements ChatScreenPresenter {

    private static final int MARK_AS_READ_DELAY = 2000;

    @Inject ChatDelegate chatDelegate;
    @Inject ChatMessageManager chatMessageManager;
    @Inject ChatToolbarMenuProvider chatToolbarMenuProvider;
    @Inject ChatContextualMenuProvider contextualMenuProvider;
    @Inject ChatUserInteractionHelper chatUserInteractionHelper;
    @Inject ChatTypingDelegate chatTypingDelegate;
    @Inject ConversationAnalyticsDelegate conversationAnalyticsDelegate;
    @Inject ChatStateDelegate chatStateDelegate;
    @Inject ConversationsDAO conversationDAO;
    @Inject MessageDAO messageDAO;
    @Inject NotificationDelegate notificationDelegate;
    @Inject OpenedConversationTracker openedConversationTracker;
    @Inject StartChatDelegate startChatDelegate;
    @Inject MessageTranslationDelegate messageTranslationDelegate;
    @Inject AttachmentMenuProvider attachmentMenuProvider;
    @Inject MessengerMediaPickerDelegate messengerMediaPickerDelegate;
    @Inject PickLocationDelegate pickLocationDelegate;

    protected String conversationId;

    private long openScreenTime;

    private Subscription messageStreamSubscription;
    private FlaggingPresenter flaggingPresenter;

    public ChatScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context, injector);
        this.conversationId = conversationId;
        openScreenTime = System.currentTimeMillis();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        flaggingPresenter = getView().getFlaggingView().getPresenter();

        notificationDelegate.cancel(MessengerNotificationFactory.MESSENGER_TAG);
        connectConnectivityStatusStream();
        connectConversationStream();
        connectToChatEvents();
        connectChatTypingStream();
        connectToLastVisibleItemStream();
        connectToShareLocationsStream();
        bindChatDelegate();
        connectToPhotoPicker();

        getViewState().setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
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
        disconnectFromPhotoPicker();
        super.onDetachedFromWindow();
    }

    ///////////////////////////////////////////////////
    ////// Streams
    //////////////////////////////////////////////////

    private void bindChatDelegate() {
        chatDelegate.bind(connectionStatusStream, conversationId)
                .compose(bindViewIoToMainComposer())
                .subscribe(this::handlePaginationStatus);
    }

    private void connectToChatEvents() {
        getView().getAttachmentClickStream()
                .distinctUntilChanged()
                .compose(bindViewIoToMainComposer())
                .subscribe(attachmentId -> chatUserInteractionHelper.openPhotoInFullScreen(attachmentId, conversationId, openScreenTime));
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

        source.compose(new NonNullFilter<>())
                .take(1)
                .subscribe(conversationUsersPair -> conversationAnalyticsDelegate
                                .trackOpenedConversation(conversationUsersPair.first,
                                        conversationUsersPair.second),
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
        getView().setConversation(conversation);
        getView().setTitle(conversation, participants);
    }

    private Subscription connectMessagesStream(long syncTime) {
        return messageDAO
                .getMessagesBySyncTime(conversationId, syncTime)
                .filter(cursor -> cursor.getCount() > 0)
                .compose(bindViewIoToMainComposer())
                .subscribe(cursor -> getView().showMessages(cursor),
                        e -> Timber.w("Unable to get messages"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Message Pagination
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onNextPageReached() {
        chatDelegate.loadNextPage();
    }

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

    ///////////////////////////////////////////////////////////////////////////
    ////// Chat typing state logic
    //////////////////////////////////////////////////////////////////////////

    private void connectChatStateDelegate() {
        chatStateDelegate.init(conversationId);
        chatStateDelegate.connectTypingStartAction(getView().getEditMessageObservable())
                .compose(bindVisibility())
                .subscribe();
        chatStateDelegate.connectTypingStopAction(getView().getEditMessageObservable())
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

    private void connectToLastVisibleItemStream() {
        getView().getLastVisibleItemStream().throttleLast(MARK_AS_READ_DELAY, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .filter(lastMessage -> isConnectionPresent())
                .compose(bindViewIoToMainComposer())
                .subscribe(this::tryMarkAsReadMessage,
                        e -> Timber.w("Unable to submitOneChatAction"));
    }

    private void tryMarkAsReadMessage(DataMessage lastMessage) {
        getView().setShowMarkUnreadMessage(false);
        chatDelegate.tryMarkAsReadMessage(lastMessage);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Message process
    ///////////////////////////////////////////////////////////////////////////

    private void connectShowSendMessageAction() {
        getView().getEditMessageObservable()
                .compose(bindVisibility())
                .subscribe(text -> {
                    getView().enableSendMessageButton(TextUtils.getTrimmedLength(text) > 0);
                });
    }

    @Override
    public boolean sendMessage(String message) {
        String finalMessage = message.trim();

        if (TextUtils.isEmpty(finalMessage)) return false;
        else {
            chatMessageManager.sendMessage(conversationId, message);
            return true;
        }
    }

    @Override
    public void retrySendMessage(DataMessage dataMessage) {
        chatMessageManager.retrySendMessage(conversationId, dataMessage);
    }

    @Override
    public void onCopyMessageTextToClipboard(DataMessage dataMessage) {
        chatUserInteractionHelper.copyToClipboard(context, dataMessage.getId());
    }

    @Override
    public void onFlagMessage(DataMessage dataMessage) {
        flaggingPresenter.flagMessage(dataMessage.getConversationId(), dataMessage.getId());
    }

    @Override
    public void onTranslateMessage(DataMessage message) {
        messageTranslationDelegate.translateMessage(message);
    }

    @Override
    public void onRevertTranslate(DataMessage message) {
        messageTranslationDelegate.revertTranslation(message);
    }

    @Override
    public void onStartNewChatForMessageOwner(DataMessage message) {
        Action1<DataConversation> crossingAction = conversation -> {
            History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
            history.pop();
            history.push(new ChatPath(conversation.getId()));
            Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
        };
        startChatDelegate.startSingleChat(message.getFromId(), crossingAction);
    }

    @Override
    public void openUserProfile(DataUser dataUser) {
        chatUserInteractionHelper.openUserProfile(dataUser);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_chat;
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {
        chatToolbarMenuProvider.provideChatMenu(conversationId, menu)
                .compose(bindView())
                .subscribe(modifiedMenu -> {
                }, e -> Timber.w("Unable to modify menu"));
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                TrackingHelper.addPeopleToChat();
                Flow.get(getContext()).set(new ExistingChatPath(conversationId));
                return true;
            case R.id.action_settings:
                conversationDAO.getConversation(conversationId)
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
                .provideMenu(message, conversationId)
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
                hidePhotoPicker();
                pickLocationDelegate.pickLocation();
                break;
            case AttachmentMenuItem.IMAGE:
                showPhotoPicker();
                break;
        }
    }

    private void hidePhotoPicker() {
        messengerMediaPickerDelegate.hidePhotoPicker();
    }

    private void showPhotoPicker() {
        messengerMediaPickerDelegate.showMultiPhotoPicker();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Timestamp
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onTimestampViewClicked(int position) {
        getView().refreshChatTimestampView(position);
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
                        sendLocation(notification.getValue());
                    } else if (notification.isOnError()) {
                        getView().showPickLocationError();
                    }
                });
    }

    private void sendLocation(Location location) {
        chatMessageManager.sendLocation(conversationId, location);
    }

    @Override
    public void onMapClicked(LatLng latLng) {
        new ExternalMapLauncher(context)
                .setLocationWithMarker(latLng.latitude, latLng.longitude)
                .setZoomLevel(LiteMapInflater.ZOOM_LEVEL)
                .launch();
    }

    private void connectToPhotoPicker() {
        messengerMediaPickerDelegate.register();
        messengerMediaPickerDelegate
                .getImagePathsStream()
                .compose(bindViewIoToMainComposer())
                .subscribe(photos -> {
                    hidePhotoPicker();
                    sendImages(Queryable.from(photos)
                            .toList());
                }, e -> Timber.e(e, "Error while image picking"));
    }

    private void sendImages(List<String> filePaths) {
        chatMessageManager.sendImages(conversationId, filePaths);
    }

    private void disconnectFromPhotoPicker() {
        messengerMediaPickerDelegate.unregister();
    }

    ///////////////////////////////////////////////////////////////////////////
    // State
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onNewViewState() {
        state = new ChatLayoutViewState();
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

}
