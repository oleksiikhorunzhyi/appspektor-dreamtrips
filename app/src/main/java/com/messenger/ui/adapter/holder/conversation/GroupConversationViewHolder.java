package com.messenger.ui.adapter.holder.conversation;

import android.view.View;

import com.messenger.entities.DataConversation;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class GroupConversationViewHolder extends BaseGroupConversationViewHolder {

    @InjectView(R.id.conversation_group_avatars_view)
    GroupAvatarsView groupAvatarsView;

    public GroupConversationViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindConversation(DataConversation conversation, String participantsList, int participantsCount) {
        super.bindConversation(conversation, participantsList, participantsCount);
        groupAvatarsView.setConversationAvatar(conversation);
    }
}
