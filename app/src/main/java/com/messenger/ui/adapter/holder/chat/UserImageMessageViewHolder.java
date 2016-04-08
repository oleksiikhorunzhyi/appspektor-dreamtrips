package com.messenger.ui.adapter.holder.chat;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class UserImageMessageViewHolder extends ImageMessageViewHolder
        implements MessageHolder.UserMessageHolder {

    @InjectView(R.id.chat_item_avatar)
    public ImageView avatarImageView;
    @InjectView(R.id.chat_username)
    public TextView nameTextView;

    public UserImageMessageViewHolder(View itemView) {
        super(itemView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imagePostView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, freeSpaceForMessageRowUserMessage,
                params.bottomMargin);
        errorView.setOnClickListener(view -> reloadImage());
    }

    ///////////////////////////////////////////////////////////////////////////
    // General message logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setBubbleBackground() {
        int backgroundResource;
        if (isPreviousMessageFromTheSameUser) {
            itemView.setPadding(itemView.getPaddingLeft(), 0, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected? R.drawable.dark_grey_bubble_image_post: R.drawable.grey_bubble_image_post;
        } else {
            itemView.setPadding(itemView.getPaddingLeft(), rowVerticalMargin, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected? R.drawable.dark_grey_bubble_comics_image_post: R.drawable.grey_bubble_comics_image_post;
        }
        imagePostView.setBackgroundResource(backgroundResource);
    }

    @Override
    public void updateMessageStatusUi(boolean needMarkUnreadMessage) {
        super.updateMessageStatusUi(needMarkUnreadMessage);
        if (message.getStatus() == MessageStatus.SENT && needMarkUnreadMessage) {
            chatMessageContainer.setBackgroundResource(R.color.chat_list_item_read_unread_background);
        } else {
            chatMessageContainer.setBackgroundResource(R.color.chat_list_item_read_read_background);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Image message logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected BaseControllerListener<ImageInfo> getLoadingListener() {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                applyErrorStatusUi();
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // Someones message logic, delegate execution to SomeoneMessageHolderDelegate
    ///////////////////////////////////////////////////////////////////////////

    private UserMessageHolderDelegate someoneMessageHolderHelper =
            new UserMessageHolderDelegate(avatarImageView, nameTextView);

    @Override
    public void setPreviousMessageFromTheSameUser(boolean previousMessageFromTheSameUser) {
        super.setPreviousMessageFromTheSameUser(previousMessageFromTheSameUser);
        someoneMessageHolderHelper.setIsPreviousMessageFromTheSameUser(previousMessageFromTheSameUser);
    }

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