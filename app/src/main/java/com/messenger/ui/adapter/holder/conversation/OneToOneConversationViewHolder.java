package com.messenger.ui.adapter.holder.conversation;

import android.net.Uri;
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

    public void bindUserProperties(String username, String avatarUrl, boolean online) {
        avatarView.setOnline(online);
        avatarView.setImageURI(avatarUrl != null ? Uri.parse(avatarUrl) : null);
        nameTextView.setText(username);
    }
}
