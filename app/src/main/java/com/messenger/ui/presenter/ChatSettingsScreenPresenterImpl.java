package com.messenger.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.messenger.delegate.chat.ChatLeavingDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.edit_member.EditChatPath;
import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.Observable;

public abstract class ChatSettingsScreenPresenterImpl<C extends ChatSettingsScreen> extends MessengerPresenterImpl<C,
        ChatSettingsViewState> implements ChatSettingsScreenPresenter<C> {

    protected String conversationId;
    protected Observable<DataConversation> conversationObservable;
    protected Observable<List<DataUser>> participantsObservable;

    protected final ChatLeavingDelegate chatLeavingDelegate;

    @Inject
    DataUser user;
    @Inject
    MessengerServerFacade facade;

    @Inject
    ConversationsDAO conversationsDAO;

    public ChatSettingsScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context, injector);
        chatLeavingDelegate = new ChatLeavingDelegate(injector, onChatLeftListener);
        this.conversationId = conversationId;
    }

    @Override
    public void onNewViewState() {
        state = new ChatSettingsViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        getView().showContent();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        connectToConversation();

        // TODO Implement this
        // getView().setNotificationSettingStatus();
    }

    private void connectToConversation() {
        Observable<Pair<DataConversation, List<DataUser>>> conversationWithParticipantObservable =
                conversationsDAO.getConversationWithParticipants(conversationId)
                .compose(new NonNullFilter<>())
                .compose(bindViewIoToMainComposer());

        conversationWithParticipantObservable
                .subscribe(conversation -> onConversationChanged(conversation.first, conversation.second));

        Observable<Pair<DataConversation, List<DataUser>>> conversationWithParticipantReplayObservable =
                conversationWithParticipantObservable.take(1).replay(1).autoConnect();

        conversationObservable = conversationWithParticipantReplayObservable
                .map(conversationWithParticipant -> conversationWithParticipant.first);

        participantsObservable = conversationWithParticipantReplayObservable
                .map(conversationWithParticipant -> conversationWithParticipant.second);
    }

    @Override
    public void applyViewState() {
        if (!isViewAttached()) {
            return;
        }
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

    protected void onConversationChanged(DataConversation conversation, List<DataUser> participants) {
        ChatSettingsScreen screen = getView();
        screen.prepareViewForOwner(isUserOwner(conversation));
        screen.setConversation(conversation);
        screen.setParticipants(conversation, participants);
    }

    //////////////////////////////////////////////////////////////////////////
    // Settings UI actions
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            chatLeavingDelegate.register();
        } else {
            chatLeavingDelegate.unregister();
        }
    }

    @Override
    public void onClearChatHistoryClicked() {
    }

    @Override
    public void onLeaveChatClicked() {
        TrackingHelper.leaveConversation();
        conversationObservable.subscribe(conversation -> chatLeavingDelegate.leave(conversation));
    }

    private final OnChatLeftListener onChatLeftListener = new OnChatLeftListener() {
        @Override
        public void onChatLeft(String conversationId, String userId) {
            if (userId.equals(user.getId())) {
                Flow flow = Flow.get(getContext());
                History newHistory = flow.getHistory()
                        .buildUpon().clear().push(ConversationsPath.MASTER_PATH)
                        .build();
                flow.setHistory(newHistory, Flow.Direction.FORWARD);
            }
        }
    };

    @Override
    public void onNotificationsSwitchClicked(boolean isChecked) {

    }

    @Override
    public void onMembersRowClicked() {
        Flow.get(getContext()).set(new EditChatPath(conversationId));
    }

    public void onEditChatName() {
        conversationObservable
                .map(conversation -> conversation.getSubject())
                .subscribe(subject -> getView().showSubjectDialog(subject));
    }

    @Override
    public void onLeaveButtonClick() {
        if (currentConnectivityStatus != SyncStatus.CONNECTED) return;

        conversationObservable
                .map(this::getLeaveConversationMessage)
                .subscribe(message -> getView().showLeaveChatDialog(message));
    }

    protected String getLeaveConversationMessage(DataConversation conversation) {
        String subject = conversation.getSubject();
        if (TextUtils.isEmpty(subject)) {
            return context.getString(R.string.chat_settings_leave_group_chat);
        } else {
            return String.format(context.getString(R.string.chat_settings_leave_group_chat_format), subject);
        }
    }

    @Override
    public void applyNewChatSubject(String subject) {
        final String newSubject = subject == null? null : subject.trim();

        Observable<GroupChat> multiUserChatObservable = facade.getChatManager()
                .createGroupChatObservable(conversationId, facade.getUsername())
                .flatMap(multiUserChat -> multiUserChat.setSubject(newSubject));

        Observable.zip(multiUserChatObservable, conversationObservable.first(),
                (multiUserChat, conversation) -> conversation)
                .compose(new IoToMainComposer<>())
                .subscribe(conversation -> {
                    conversation.setSubject(newSubject);
                    conversationsDAO.save(conversation);
                    getView().setConversation(conversation);
                }, throwable -> {
                    getView().showErrorDialog(R.string.chat_settings_error_change_subject);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_chat_settings;
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {
        conversationsDAO.getConversation(conversationId)
                .compose(bindViewIoToMainComposer())
                .take(1)
                .subscribe(conversation -> {
                boolean isMultiUserChat = !ConversationHelper.isSingleChat(conversation);
                if (!isMultiUserChat || !isUserOwner(conversation)) {
                    menu.findItem(R.id.action_overflow).setVisible(false);
                    return;
                }
                if (ConversationHelper.isTripChat(conversation)) {
                    menu.findItem(R.id.action_change_chat_avatar).setVisible(false);
                    menu.findItem(R.id.action_remove_chat_avatar).setVisible(false);
                }
                if (TextUtils.isEmpty(conversation.getAvatar())) {
                    menu.findItem(R.id.action_remove_chat_avatar).setVisible(false);
                }
            });

    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_overflow:
                // overflow menu click, do nothing, wait for actual actions clicks
                return true;
            case R.id.action_edit_chat_name:
                onEditChatName();
                return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////
    ///// Helpers
    ////////////////////////////////////////////////////

    private boolean isUserOwner(DataConversation conversation) {
        return TextUtils.equals(conversation.getOwnerId(), user.getId());
    }
}
