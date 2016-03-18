package com.messenger.ui.adapter.holder;

import android.view.View;

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
    protected void setConversationId(String conversationId) {
        groupAvatarsView.setConversationAvatar(conversationId);
    }
}
