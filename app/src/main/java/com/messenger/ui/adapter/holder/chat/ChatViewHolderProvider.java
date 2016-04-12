package com.messenger.ui.adapter.holder.chat;

import android.content.res.Resources;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.worldventures.dreamtrips.R;

import javax.inject.Inject;

public class ChatViewHolderProvider {

    private static final float MESSAGE_SCREEN_WIDTH_SHARE = 0.6f;

    // Use this variable as margins that determine free space left in row after message and avatar took up
    // the space needed.
    private int freeSpaceForMessageRowOwnMessage;
    private int freeSpaceForMessageRowUserMessage;

    private static final int VIEW_TYPE_OWN_TEXT_MESSAGE = 1;
    private static final int VIEW_TYPE_USER_TEXT_MESSAGE = 2;
    private static final int VIEW_TYPE_OWN_IMAGE_MESSAGE = 3;
    private static final int VIEW_TYPE_USER_IMAGE_MESSAGE = 4;

    private DataUser currentUser;

    @Inject
    public ChatViewHolderProvider(DataUser currentUser) {
        this.currentUser = currentUser;
    }

    public MessageViewHolder provideViewHolder(ViewGroup parent, int viewType) {
        Resources res = parent.getResources();
        int screenWidth = res.getDisplayMetrics().widthPixels;
        int messageWidth = (int) (screenWidth * MESSAGE_SCREEN_WIDTH_SHARE);
        int ownMessageWidth = 2 * res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + messageWidth;
        freeSpaceForMessageRowOwnMessage = screenWidth - ownMessageWidth;
        int userMessageWidth = ownMessageWidth + res.getDimensionPixelSize(R.dimen.chat_list_item_horizontal_padding)
                + res.getDimensionPixelSize(R.dimen.list_item_small_avatar_image_size);
        freeSpaceForMessageRowUserMessage = screenWidth - userMessageWidth;

        MessageViewHolder messageViewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_OWN_TEXT_MESSAGE:
                messageViewHolder = new OwnTextMessageViewHolder(inflateRow(parent,
                        R.layout.list_item_chat_own_text_messsage));
                setMarginForOwnMessage(messageViewHolder);
                break;
            case VIEW_TYPE_USER_TEXT_MESSAGE:
                messageViewHolder = new UserTextMessageViewHolder(inflateRow(parent,
                        R.layout.list_item_chat_user_text_message));
                setMarginForUserMessage(messageViewHolder);
                break;
            case VIEW_TYPE_OWN_IMAGE_MESSAGE:
                messageViewHolder = new OwnImageMessageViewHolder(inflateRow(parent,
                        R.layout.list_item_chat_own_image_message));
                setMarginForOwnMessage(messageViewHolder);
                break;
            case VIEW_TYPE_USER_IMAGE_MESSAGE:
                messageViewHolder = new UserImageMessageViewHolder(inflateRow(parent,
                        R.layout.list_item_chat_user_image_message));
                setMarginForUserMessage(messageViewHolder);
                break;
        }

        return messageViewHolder;
    }

    private void setMarginForOwnMessage(MessageViewHolder messageViewHolder) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageViewHolder.
                messageContainer.getLayoutParams();
        params.setMargins(freeSpaceForMessageRowOwnMessage, params.topMargin, params.rightMargin,
                params.bottomMargin);
    }

    private void setMarginForUserMessage(MessageViewHolder messageViewHolder) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageViewHolder.
                messageContainer.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, freeSpaceForMessageRowUserMessage,
                params.bottomMargin);
    }

    public int provideViewType(Cursor cursor, int position) {
        cursor.moveToPosition(position);

        boolean ownMessage = isOwnMessage(cursor);
        String attachmentType = cursor.getString(cursor.getColumnIndex(DataAttachment$Table.TYPE));

        int viewType;
        if (TextUtils.equals(attachmentType, AttachmentType.IMAGE)) {
            viewType = ownMessage ? VIEW_TYPE_OWN_IMAGE_MESSAGE : VIEW_TYPE_USER_IMAGE_MESSAGE;
        } else {
            viewType = ownMessage ? VIEW_TYPE_OWN_TEXT_MESSAGE : VIEW_TYPE_USER_TEXT_MESSAGE;
        }

        return viewType;
    }

    private View inflateRow(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    private boolean isOwnMessage(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID))
                .equals(currentUser.getId());
    }
}
