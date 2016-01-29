package com.messenger.ui.adapter.holder;

import android.net.Uri;
import android.view.View;

import com.messenger.entities.Conversation;
import com.messenger.entities.User;
import com.messenger.ui.widget.AvatarView;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.InjectView;

public class OneToOneConversationViewHolder extends BaseConversationViewHolder {

    @InjectView(R.id.conversation_avatar_view)
    AvatarView avatarView;

    public OneToOneConversationViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void setConversationPicture(List<User> participants) {
        User addressee = participants.get(0);
        avatarView.setImageURI(Uri.parse(addressee.getAvatarUrl()));
    }

    @Override
    protected void onParticipantsLoaded(Conversation conversation, List<User> participants) {
        super.onParticipantsLoaded(conversation, participants);
        if (participants == null || participants.isEmpty()) return;
        avatarView.setOnline(participants.get(0).isOnline());
    }
}
