package com.messenger.ui.adapter.holder;

import android.view.View;

import com.messenger.entities.DataUser;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.InjectView;

public class GroupConversationViewHolder extends BaseConversationViewHolder{

    @InjectView(R.id.conversation_group_avatars_view)
    GroupAvatarsView groupAvatarsView;

    public GroupConversationViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void setConversationId(String conversationId) {
        groupAvatarsView.setConversationAvatar(conversationId);
    }

    @Override
    protected void setParticipants(List<DataUser> participants) {

    }
}
