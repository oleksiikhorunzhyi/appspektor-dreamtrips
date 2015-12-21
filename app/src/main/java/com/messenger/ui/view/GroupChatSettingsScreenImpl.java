package com.messenger.ui.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.widget.ChatSettingsRow;
import com.messenger.util.UiUtils;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;

public class GroupChatSettingsScreenImpl extends ChatSettingsScreenImpl {

    @InjectView(R.id.chat_settings_group_chat_info_textview)
    TextView groupChatInfoTextView;

    private ChatSettingsRow membersSettingsRow;

    public GroupChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupChatSettingsScreenImpl(Context context) {
        super(context);
    }

    @Override
    public void setConversation(Conversation conversation) {
        toolbarPresenter.setTitle(R.string.chat_settings_group_chat);
    }

    @Override
    public void setParticipants(Conversation conversation, List<User> participants) {
        groupAvatarsView.setVisibility(View.VISIBLE);
        groupAvatarsView.updateAvatars(participants);
        chatNameTextView.setText(UiUtils.getGroupConversationName(conversation, participants));
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
}
