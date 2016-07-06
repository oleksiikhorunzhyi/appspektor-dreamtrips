package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.inflater.MessageCommonInflater;
import com.messenger.ui.adapter.inflater.UserMessageHolderInflater;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

public class UserMessageViewHolder extends MessageViewHolder {

    protected static final float ALPHA_MESSAGE_SENDING = 0.5f;
    protected static final float ALPHA_MESSAGE_NORMAL = 1f;

    protected DataAttachment dataAttachment;
    protected DataTranslation dataTranslation;

    @Optional
    @InjectView(R.id.view_retry_send)
    public View viewRetrySend;

    protected boolean isGroupMessage;
    protected boolean previousMessageFromSameUser;

    private final MessageCommonInflater messageCommonInflater;
    private final UserMessageHolderInflater userMessageHolderInflater;

    public UserMessageViewHolder(View itemView) {
        super(itemView);
        messageCommonInflater = new MessageCommonInflater(itemView);
        userMessageHolderInflater = new UserMessageHolderInflater(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        previousMessageFromSameUser = previousMessageIsFromSameUser(cursor);
        dataAttachment = SqlUtils.convertToModel(true, DataAttachment.class, cursor);
        boolean translationExist = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(MessageDAO.TRANSLATION_ID)));
        dataTranslation = translationExist ? SqlUtils.convertToModel(true, DataTranslation.class, cursor) : null;
        isGroupMessage = !TextUtils.equals(conversationType, ConversationType.CHAT);
        //
        messageCommonInflater.onCellBind(previousMessageFromSameUser, shouldMarkAsUnread() && isUnread(), selected);
        userMessageHolderInflater.onCellBind(dataUserSender, isGroupMessage, previousMessageFromSameUser);
    }

    @Optional
    @OnLongClick(R.id.message_container)
    boolean onMessageLongClicked() {
        cellDelegate.onMessageLongClicked(dataMessage);
        return true;
    }

    @Optional
    @OnClick(R.id.view_retry_send)
    void onRetry() {
        cellDelegate.onRetryClicked(dataMessage);
    }

    @Optional
    @OnClick(R.id.chat_item_avatar)
    void onUserAvatarClicked() {
        cellDelegate.onAvatarClicked(dataUserSender);
    }

    private boolean previousMessageIsFromSameUser(Cursor cursor) {
        String currentId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        if (!cursor.moveToPrevious()) {
            cursor.moveToNext();
            return false;
        }
        String prevMessageType = cursor.getString(cursor.getColumnIndex(DataMessage$Table.TYPE));
        boolean prevMessageIsSystemMessage = !MessageType.MESSAGE.equals(prevMessageType);
        String prevId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        cursor.moveToNext();
        return !prevMessageIsSystemMessage && TextUtils.equals(prevId, currentId);
    }

    protected boolean isUnread() {
        return dataMessage.getStatus() == MessageStatus.SENT;
    }

    protected boolean shouldMarkAsUnread() {
        // server always keeps SENT status for our own messages,
        // make sure we don't show our own messages as unread
        return !isOwnMessage && needMarkUnreadMessage;
    }

}
