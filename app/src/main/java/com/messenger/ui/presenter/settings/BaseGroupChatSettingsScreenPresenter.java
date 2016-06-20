package com.messenger.ui.presenter.settings;

import android.content.Context;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.chat.ChatGroupCommandsInteractor;
import com.messenger.delegate.chat.command.LeaveChatCommand;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.synchmechanism.SyncStatus;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.chat.ChatPath;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.edit_member.EditChatPath;
import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.messenger.ui.viewstate.ChatSettingsViewState.UploadingState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import timber.log.Timber;

public abstract class BaseGroupChatSettingsScreenPresenter extends BaseChatSettingsScreenPresenter<GroupChatSettingsScreen>
        implements GroupChatSettingsScreenPresenter {

    @Inject ChatGroupCommandsInteractor chatGroupCommandsInteractor;
    public BaseGroupChatSettingsScreenPresenter(Context context, Injector injector, String conversationId) {
        super(context, injector, conversationId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        TrackingHelper.groupSettingsOpened();
    }

    @Override
    protected void onConversationChanged(DataConversation conversation, List<DataUser> participants) {
        super.onConversationChanged(conversation, participants);
        DataUser owner = Queryable.from(participants)
                .filter(user -> ConversationHelper.isOwner(conversation, user))
                .firstOrDefault();
        getView().setOwner(owner);
        getView().setLeaveButtonVisible(ConversationHelper.isPresent(conversation)
                && !ConversationHelper.isOwner(conversation, currentUser));
    }

    protected void onChangeAvatarSuccess() {
        getViewState().setUploadAvatar(UploadingState.UPLOADED);
        GroupChatSettingsScreen screen = getView();
        if (screen != null) {
            screen.invalidateToolbarMenu();
            screen.hideChangingAvatarProgressBar();
        }
    }

    protected void onChangeAvatarFailed(Throwable throwable) {
        getViewState().setUploadAvatar(UploadingState.ERROR);
        Timber.e(throwable, "");
        GroupChatSettingsScreen screen = getView();
        if (screen != null) {
            screen.hideChangingAvatarProgressBar();
            screen.showErrorDialog(R.string.chat_settings_error_changing_avatar_subject);
        }
    }

    @Override
    public void onLeaveChatClicked() {
        TrackingHelper.leaveConversation();
        chatGroupCommandsInteractor.getLeaveChatPipe()
                .createObservableResult(new LeaveChatCommand(conversationId))
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> {
                    Flow flow = Flow.get(getContext());
                    History history = flow.getHistory()
                            .buildUpon().clear()
                            .push(ConversationsPath.MASTER_PATH)
                            .push(new ChatPath(command.getConversationId()))
                            .build();
                    flow.setHistory(history, Flow.Direction.BACKWARD);
                }, e -> Timber.e(e, "Can't leave chat"));
    }

    @Override
    public void onMembersRowClicked() {
        Flow.get(getContext()).set(new EditChatPath(conversationId));
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
        throw new IllegalStateException("Method was not implemented");
    }
}
