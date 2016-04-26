package com.messenger.ui.adapter.inflater;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class UserMessageHolderInflater {

    private DataUser dataUser;
    private DataConversation dataConversation;
    private boolean previousMessageFromSameUser;

    @Optional
    @InjectView(R.id.chat_item_avatar)
    ImageView avatarImageView;
    @Optional
    @InjectView(R.id.chat_username)
    TextView nameTextView;

    public UserMessageHolderInflater(View itemView) {
        ButterKnife.inject(this, itemView);
    }

    public void onCellBind(DataUser dataUser, DataConversation dataConversation,
                           boolean previousMessageFromSameUser) {
        this.dataUser = dataUser;
        this.dataConversation = dataConversation;
        this.previousMessageFromSameUser = previousMessageFromSameUser;

        if (avatarImageView != null) updateAvatar();
        if (nameTextView != null) updateName();
    }

    private void updateAvatar() {
        if (previousMessageFromSameUser) {
            avatarImageView.setVisibility(View.INVISIBLE);
        } else {
            avatarImageView.setVisibility(View.VISIBLE);
            avatarImageView.setImageURI(dataUser == null || dataUser.getAvatarUrl() == null ?
                    null : Uri.parse(dataUser.getAvatarUrl()));
        }
    }

    private void updateName() {
        if (ConversationHelper.isGroup(dataConversation)
                && dataUser != null
                && !previousMessageFromSameUser) {
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(dataUser.getName());
        } else {
            nameTextView.setVisibility(View.GONE);
        }
    }
}