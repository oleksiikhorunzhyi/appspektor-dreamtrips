package com.messenger.ui.adapter.holder;

import android.net.Uri;
import android.view.View;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
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
    protected void setConversationPicture(List<DataUser> participants) {
        DataUser addressee = participants.get(0);
        avatarView.setImageURI(Uri.parse(addressee.getAvatarUrl()));
    }

    @Override
    protected void onParticipantsLoaded(DataConversation conversation, List<DataUser> participants) {
        super.onParticipantsLoaded(conversation, participants);
        if (participants == null || participants.isEmpty()) return;
        avatarView.setOnline(participants.get(0).isOnline());
    }
}
