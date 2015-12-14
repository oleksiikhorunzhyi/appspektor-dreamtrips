package com.messenger.ui.adapter.holder;

import android.view.View;

import com.messenger.ui.widget.AvatarView;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class OneToOneConversationViewHolder extends BaseConversationViewHolder {

    @InjectView(R.id.conversation_avatar_view)
    AvatarView avatarView;

    public OneToOneConversationViewHolder(View itemView) {
        super(itemView);
    }

    public AvatarView getAvatarView() {
        return avatarView;
    }
}
