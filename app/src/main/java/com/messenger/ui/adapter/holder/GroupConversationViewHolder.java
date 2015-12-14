package com.messenger.ui.adapter.holder;

import android.view.View;

import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class GroupConversationViewHolder extends BaseConversationViewHolder{

    @InjectView(R.id.conversation_group_avatars_view)
    GroupAvatarsView groupAvatarsView;

    public GroupConversationViewHolder(View itemView) {
        super(itemView);
    }

    public GroupAvatarsView getGroupAvatarsView() {
        return groupAvatarsView;
    }
}
