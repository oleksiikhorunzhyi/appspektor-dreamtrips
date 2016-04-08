package com.messenger.ui.adapter.holder.chat;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.messenger.ui.helper.ConversationHelper;

/**
 * Contains logic such as updating avatar, user name textview and read status background
 */
public class UserMessageHolderDelegate implements MessageHolder.UserMessageHolder {
    private DataUser user;

    private ImageView avatarImageView;
    private TextView nameTextView;

    private boolean isPreviousMessageFromTheSameUser;

    public UserMessageHolderDelegate(ImageView avatarImageView, TextView userNameTextView) {
        this.avatarImageView = avatarImageView;
        this.nameTextView = userNameTextView;
    }

    @Override
    public void setAuthor(DataUser user) {
        this.user = user;
    }

    @Override
    public void setAvatarClickListener(MessagesCursorAdapter.OnAvatarClickListener listener) {
        avatarImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAvatarClick(user);
            }
        });
    }

    public void setIsPreviousMessageFromTheSameUser(boolean isPreviousMessageFromTheSameUser) {
        this.isPreviousMessageFromTheSameUser = isPreviousMessageFromTheSameUser;
    }

    @Override
    public void updateAvatar() {
        if (isPreviousMessageFromTheSameUser) {
            avatarImageView.setVisibility(View.INVISIBLE);
        } else {
            avatarImageView.setVisibility(View.VISIBLE);
            avatarImageView.setImageURI(user == null || user.getAvatarUrl() == null ? null : Uri.parse(user.getAvatarUrl()));
        }
    }

    @Override
    public void updateName(DataConversation dataConversation) {
        if (ConversationHelper.isGroup(dataConversation) && user != null
                && !isPreviousMessageFromTheSameUser) {
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(user.getName());
        } else {
            nameTextView.setVisibility(View.GONE);
        }
    }
}