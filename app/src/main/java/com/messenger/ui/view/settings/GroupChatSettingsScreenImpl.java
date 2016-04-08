package com.messenger.ui.view.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.presenter.ChatSettingsScreenPresenter;
import com.messenger.ui.presenter.MultiChatSettingsScreenPresenter;
import com.messenger.ui.util.avatar.ChangeAvatarDelegate;
import com.messenger.ui.widget.ChatSettingsRow;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Observable;

public class GroupChatSettingsScreenImpl<P extends GroupSettingsPath> extends ChatSettingsScreenImpl<GroupChatSettingsScreen, P>
    implements GroupChatSettingsScreen {

    @InjectView(R.id.chat_settings_group_avatars_view_progress_bar)
    ProgressBar groupAvatarsViewProgressBar;

    private ChatSettingsRow membersSettingsRow;
    private ConversationHelper conversationHelper;

    @Inject
    ChangeAvatarDelegate changeAvatarDelegate;

    public GroupChatSettingsScreenImpl(Context context) {
        super(context);
    }

    public GroupChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initUi() {
        injector.inject(this);
        conversationHelper = new ConversationHelper();
        super.initUi();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        changeAvatarDelegate.register();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        changeAvatarDelegate.unregister();
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
    public void setParticipants(DataConversation conversation, List<DataUser> participants) {
        conversationHelper.setTitle(chatNameTextView, conversation, participants, false);
        String chatDescriptionFormat = getContext()
                .getString(R.string.chat_settings_group_chat_description);
        int onlineCount = 0;
        for (DataUser user : participants) {
            if (user.isOnline()) {
                onlineCount++;
            }
        }
        String chatDescription = String.format(chatDescriptionFormat, participants.size(),
                onlineCount);
        chatDescriptionTextView.setText(chatDescription);

        if (membersSettingsRow == null) {
            membersSettingsRow = new ChatSettingsRow(getContext());
            chatSettingsRows.addView(membersSettingsRow);
            membersSettingsRow.setIcon(R.drawable.ic_people_black_24_px);
            membersSettingsRow.setOnClickListener(v -> getPresenter().onMembersRowClicked());
        }
        String membersFormat = getContext().getString(R.string.chat_settings_row_members_format);
        membersSettingsRow.setTitle(String.format(membersFormat, participants.size()));
    }

    @Override
    protected int getLeaveChatButtonStringRes() {
        return R.string.chat_settings_row_leave_chat;
    }

    @NonNull
    @Override
    public ChatSettingsScreenPresenter<GroupChatSettingsScreen> createPresenter() {
        return new MultiChatSettingsScreenPresenter(getContext(), injector, getPath().getConversationId());
    }

    @Override
    public Observable<ChosenImage> getAvatarImagesStream() {
        return changeAvatarDelegate.getAvatarImagesStream();
    }

    @Override
    public void showAvatarPhotoPicker() {
        changeAvatarDelegate.showAvatarPhotoPicker();
    }

    @Override
    public void hideAvatarPhotoPicker() {
        changeAvatarDelegate.hideAvatarPhotoPicker();
    }

    @Override
    public void showChangingAvatarProgressBar() {
        groupAvatarsViewProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideChangingAvatarProgressBar() {
        groupAvatarsViewProgressBar.setVisibility(View.GONE);
    }
}
