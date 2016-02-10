package com.messenger.ui.adapter.holder.chat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class UserTextMessageViewHolder extends TextMessageViewHolder implements MessageHolder.UserMessageHolder {

    @InjectView(R.id.chat_item_avatar)
    public ImageView avatarImageView;
    @InjectView(R.id.chat_username)
    public TextView nameTextView;

    public UserTextMessageViewHolder(View itemView) {
        super(itemView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageTextView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, freeSpaceForMessageRowUserMessage,
                params.bottomMargin);
    }

    ///////////////////////////////////////////////////////////////////////////
    // General messages logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setBubbleBackground() {
        int backgroundResource;
        if (isPreviousMessageFromTheSameUser) {
            itemView.setPadding(itemView.getPaddingLeft(), 0, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected ? R.drawable.dark_grey_bubble
                    : R.drawable.grey_bubble;
        } else {
            itemView.setPadding(itemView.getPaddingLeft(), rowVerticalMargin, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected ? R.drawable.dark_grey_bubble_comics
                    : R.drawable.grey_bubble_comics;
        }
        messageTextView.setBackgroundResource(backgroundResource);
    }

    @Override
    public void updateMessageStatusUi(boolean needMarkUnreadMessage) {
        if (message.getStatus() == MessageStatus.SENT && needMarkUnreadMessage) {
            chatMessageContainer.setBackgroundResource(R.color.chat_list_item_read_unread_background);
        } else {
            chatMessageContainer.setBackgroundResource(R.color.chat_list_item_read_read_background);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Someones message logic, delegate execution to SomeoneMessageHolderDelegate
    ///////////////////////////////////////////////////////////////////////////

    private UserMessageHolderDelegate someoneMessageHolderHelper =
            new UserMessageHolderDelegate(avatarImageView, nameTextView);

    @Override
    public void setAuthor(DataUser user) {
        someoneMessageHolderHelper.setAuthor(user);
    }

    @Override
    public void setAvatarClickListener(MessagesCursorAdapter.OnAvatarClickListener listener) {
        someoneMessageHolderHelper.setAvatarClickListener(listener);
    }

    @Override
    public void updateAvatar() {
        someoneMessageHolderHelper.updateAvatar();
    }

    @Override
    public void updateName(DataConversation dataConversation) {
        someoneMessageHolderHelper.updateName(dataConversation);
    }
}