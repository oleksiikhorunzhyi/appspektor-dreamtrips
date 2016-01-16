package com.messenger.ui.adapter.holder;

import android.view.View;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.widget.AvatarView;
import com.messenger.util.Constants;
import com.squareup.picasso.Picasso;
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
            Picasso.with(context)
                    .load(addressee.getAvatarUrl())
                    .placeholder(Constants.PLACEHOLDER_USER_AVATAR_BIG)
                    .into(avatarView);
    }

    @Override
    protected void onParticipantsLoaded(Conversation conversation, List<User> participants) {
        super.onParticipantsLoaded(conversation, participants);
        if (participants == null || participants.isEmpty()) return;
        avatarView.setOnline(participants.get(0).isOnline());
    }
}
