package com.messenger.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.presenter.ChatSettingsScreenPresenter;
import com.messenger.ui.presenter.MultiChatSettingsScreenPresenter;
import com.messenger.ui.widget.ChatSettingsRow;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.InjectView;

public class GroupChatSettingsScreenImpl extends ChatSettingsScreenImpl {

    @InjectView(R.id.chat_settings_group_chat_info_textview)
    TextView groupChatInfoTextView;

    private ChatSettingsRow membersSettingsRow;
    private ConversationHelper conversationHelper;

    public GroupChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupChatSettingsScreenImpl(Context context) {
        super(context);
    }

    @Override
    protected void initUi() {
        conversationHelper = new ConversationHelper();
        super.initUi();
    }

    @Override
    public void setConversation(Conversation conversation) {
        super.setConversation(conversation);
        toolbarPresenter.setTitle(R.string.chat_settings_group_chat);
        if (!TextUtils.isEmpty(conversation.getSubject())) {
            chatNameTextView.setText(conversation.getSubject());
        }
    }

    @Override
    public void setParticipants(Conversation conversation, List<User> participants) {
        groupAvatarsView.setVisibility(View.VISIBLE);
        groupAvatarsView.updateAvatars(participants);
        conversationHelper.setTitle(chatNameTextView, conversation, participants, false);
        String chatDescriptionFormat = getContext()
                .getString(R.string.chat_settings_group_chat_description);
        int onlineCount = 0;
        for (User user : participants) {
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

        // we decided to not add it for now
//        groupChatInfoTextView.setVisibility(View.VISIBLE);
//        String groupChatInfoTextViewFormat = getContext().getString(R.string.chat_settings_group_chat_info_text_format);
//        User owner = participants.get(0);
//        Date date = new Date();
//        SimpleDateFormat df = new SimpleDateFormat("MM dd, yyyy");
//        String dateString = df.format(date);
//        groupChatInfoTextView.setText(String.format(groupChatInfoTextViewFormat,
//                owner.getName(), dateString));
    }

    @Override
    protected int getLeaveChatButtonStringRes() {
        return R.string.chat_settings_row_leave_chat;
    }

    @NonNull
    @Override
    public ChatSettingsScreenPresenter createPresenter() {
        Activity activity = getActivity();
        return new MultiChatSettingsScreenPresenter(activity, activity.getIntent());
    }
}
