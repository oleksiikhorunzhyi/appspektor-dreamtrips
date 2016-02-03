package com.messenger.ui.adapter.holder;

import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class MessageHolder extends ViewHolder {
    private static final float MESSAGE_SCREEN_WIDTH_SHARE = 0.6f;

    public interface OwnMessageHolder {
        void setOnRepeatMessageListener(MessagesCursorAdapter.OnRepeatMessageSend listener);
    }

    public interface SomeoneUserMessageHolder {
        void setAuthor(DataUser user);
        void setAvatarClickListener(MessagesCursorAdapter.OnAvatarClickListener listener);
        void updateAvatar();
        void updateName(DataConversation dataConversation);
    }

    @InjectView(R.id.chat_message_container)
    public View chatMessageContainer;
    @InjectView(R.id.chat_date)
    public TextView dateTextView;

    protected boolean isSelected;
    protected boolean isPreviousMessageFromTheSameUser;
    protected DataMessage message;

    // Use this variable as margins that determine free space left in row after message and avatar took up
    // the space needed.
    protected final int freeSpaceForMessageRowOwnMessage;
    protected final int freeSpaceForMessageRowUserMessage;
    protected final int rowVerticalMargin;

    public MessageHolder(View itemView) {
        super(itemView);
        Resources res = itemView.getResources();
        int screenWidth = res.getDisplayMetrics().widthPixels;
        int messageWidth = (int)(screenWidth * MESSAGE_SCREEN_WIDTH_SHARE);
        int ownMessageWidth = 2 * res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + messageWidth;
        freeSpaceForMessageRowOwnMessage = screenWidth - ownMessageWidth;
        int userMessageWidth = ownMessageWidth + res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + res.getDimensionPixelSize(R.dimen.list_item_small_avatar_image_size);
        freeSpaceForMessageRowUserMessage = screenWidth - userMessageWidth;

        rowVerticalMargin = res.getDimensionPixelSize(R.dimen.chat_list_item_row_vertical_padding);
    }

    public void setMessage(DataMessage dataMessage) {
        this.message = dataMessage;
    }

    public void setPreviousMessageFromTheSameUser(boolean previousMessageFromTheSameUser) {
        isPreviousMessageFromTheSameUser = previousMessageFromTheSameUser;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper Delegates
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Contains logic such as updating avatar, user name textview and read status background
     */
    protected class SomeoneMessageHolderDelegate implements SomeoneUserMessageHolder {
        private DataUser user;

        private ImageView avatarImageView;
        private TextView nameTextView;

        public SomeoneMessageHolderDelegate(ImageView avatarImageView, TextView userNameTextView) {
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

    public abstract void setBubbleBackground();
    public abstract void updateMessageStatusUi();
    public abstract View getViewForClickableTimestamp();
}
