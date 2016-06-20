package com.messenger.ui.view.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.dialog.ChangeSubjectDialog;
import com.messenger.ui.dialog.LeaveChatDialog;
import com.messenger.ui.helper.ConversationUIHelper;
import com.messenger.ui.presenter.settings.GroupChatSettingsScreenPresenter;
import com.messenger.ui.presenter.settings.GroupChatSettingsScreenPresenterImpl;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegate;
import com.messenger.ui.widget.ChatSettingsRow;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class GroupChatSettingsScreenImpl<P extends GroupSettingsPath>
        extends BaseChatSettingsScreen<GroupChatSettingsScreen, GroupChatSettingsScreenPresenter, P>
        implements GroupChatSettingsScreen {

    @InjectView(R.id.chat_settings_group_avatars_view_progress_bar) ProgressBar groupAvatarsViewProgressBar;

    @Inject MessengerMediaPickerDelegate messengerMediaPickerDelegate;

    private ChatSettingsRow membersSettingsRow;

    public GroupChatSettingsScreenImpl(Context context) {
        super(context);
    }

    public GroupChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initUi() {
        injector.inject(this);
        super.initUi();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        messengerMediaPickerDelegate.register();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        messengerMediaPickerDelegate.unregister();
    }

    @Override
    public void setConversation(@NonNull DataConversation conversation) {
        super.setConversation(conversation);
        toolbarPresenter.setTitle(R.string.chat_settings_group_chat);
        if (!TextUtils.isEmpty(conversation.getSubject())) {
            chatNameTextView.setText(conversation.getSubject());
        }
        groupAvatarsView.setConversationAvatar(conversation);
        groupAvatarsView.setVisibility(VISIBLE);
    }

    @Override
    public void setOwner(DataUser owner) {
        if (owner != null) {
            String createdByText = getResources()
                    .getString(R.string.chat_settings_group_chat_info_text_format, owner.getName());
            infoTextView.setVisibility(VISIBLE);
            infoTextView.setText(createdByText);
        }
    }

    @Override
    public void setParticipants(DataConversation conversation, List<DataUser> participants) {
        ConversationUIHelper.setTitle(chatNameTextView, conversation, participants, false);
        ConversationUIHelper.setSubtitle(chatDescriptionTextView, conversation, participants);

        if (membersSettingsRow == null) {
            membersSettingsRow = new ChatSettingsRow(getContext());
            chatSettingsRows.addView(membersSettingsRow);
            membersSettingsRow.setIcon(R.drawable.ic_people_black_24_px);
            membersSettingsRow.setOnClickListener(v -> getPresenter().onMembersRowClicked());
        }
        String membersFormat = getContext().getString(R.string.chat_settings_row_members_format);
        membersSettingsRow.setTitle(String.format(membersFormat, participants.size()));
    }

    @NonNull
    @Override
    public GroupChatSettingsScreenPresenter createPresenter() {
        return new GroupChatSettingsScreenPresenterImpl(getContext(), injector, getPath().getConversationId());
    }

    @Override
    public Observable<String> getAvatarImagePathsStream() {
        return messengerMediaPickerDelegate.getImagePathsStream();
    }


    @OnClick(R.id.chat_settings_leave_chat_button)
    void onLeaveChatButtonClicked() {
        getPresenter().onLeaveButtonClick();
    }

    public void showLeaveChatDialog(String message) {
        new LeaveChatDialog(getContext(), message)
                .setPositiveListener(getPresenter()::onLeaveChatClicked)
                .show();
    }

    @Override
    public void showSubjectDialog(String currentSubject) {
        new ChangeSubjectDialog(getContext(), currentSubject)
                .setPositiveListener(this::onSubjectEntered)
                .show();
    }

    private void onSubjectEntered(String subject) {
        getPresenter().applyNewChatSubject(subject);
    }

    @Override
    public void showAvatarPhotoPicker() {
        messengerMediaPickerDelegate.showPhotoPicker();
    }

    @Override
    public void hideAvatarPhotoPicker() {
        messengerMediaPickerDelegate.hidePhotoPicker();
    }

    @Override
    public void showChangingAvatarProgressBar() {
        groupAvatarsViewProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideChangingAvatarProgressBar() {
        groupAvatarsViewProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setLeaveButtonVisible(boolean visible) {
        leaveChatButton.setVisibility(visible ? VISIBLE : GONE);
    }
}
