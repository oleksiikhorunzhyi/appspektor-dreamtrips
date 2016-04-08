package com.messenger.ui.adapter.holder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
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
