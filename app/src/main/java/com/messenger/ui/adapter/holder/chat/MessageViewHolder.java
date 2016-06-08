package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.ChatCellDelegate;
import com.messenger.ui.adapter.holder.CursorViewHolder;
import com.messenger.ui.adapter.inflater.MessageCommonInflater;
import com.messenger.ui.adapter.inflater.UserMessageHolderInflater;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

public abstract class MessageViewHolder extends CursorViewHolder {

    protected static final float ALPHA_MESSAGE_SENDING = 0.5f;
    protected static final float ALPHA_MESSAGE_NORMAL = 1f;

    protected DataMessage dataMessage;
    protected DataAttachment dataAttachment;
    protected DataUser dataUserSender;
    protected DataTranslation dataTranslation;

    @InjectView(R.id.chat_date)
    public TextView dateTextView;
    @InjectView(R.id.message_container)
    public FrameLayout messageContainer;
    @Optional
    @InjectView(R.id.view_retry_send)
    public View viewRetrySend;

    protected boolean selected;
    protected boolean previousMessageFromSameUser;
    protected boolean needMarkUnreadMessage;
    protected boolean isGroupMessage;

    protected ChatCellDelegate cellDelegate;

    private final MessageCommonInflater messageCommonInflater;
    private final UserMessageHolderInflater userMessageHolderInflater;

    public MessageViewHolder(View itemView) {
        super(itemView);
        messageCommonInflater = new MessageCommonInflater(itemView);
        userMessageHolderInflater = new UserMessageHolderInflater(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        previousMessageFromSameUser = previousMessageIsFromSameUser(cursor);
        dataMessage = SqlUtils.convertToModel(true, DataMessage.class, cursor);
        dataAttachment = SqlUtils.convertToModel(true, DataAttachment.class, cursor);
        dataUserSender = SqlUtils.convertToModel(true, DataUser.class, cursor);
        boolean translationExist = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(MessageDAO.TRANSLATION_ID)));
        dataTranslation = translationExist ? SqlUtils.convertToModel(true, DataTranslation.class, cursor) : null;
        String type =  cursor.getString(cursor.getColumnIndex(MessageDAO.CONVERSATION_TYPE));
        isGroupMessage = !TextUtils.equals(type, ConversationType.CHAT);
        //
        messageCommonInflater.onCellBind(previousMessageFromSameUser, isUnread(), selected);
        userMessageHolderInflater.onCellBind(dataUserSender, isGroupMessage, previousMessageFromSameUser);
    }

    private boolean isUnread() {
        return dataMessage.getStatus() == MessageStatus.SENT && needMarkUnreadMessage;
    }

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

    public void setCellDelegate(ChatCellDelegate cellDelegate) {
        this.cellDelegate = cellDelegate;
    }

    public void setNeedMarkUnreadMessage(boolean needMarkUnreadMessage) {
        this.needMarkUnreadMessage = needMarkUnreadMessage;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public View getTimestampClickableView() {
        return messageContainer;
    }

    private boolean previousMessageIsFromSameUser(Cursor cursor) {
        String currentId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        if (!cursor.moveToPrevious()) {
            cursor.moveToNext();
            return false;
        }
        String prevId = cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID));
        cursor.moveToNext();
        return TextUtils.equals(prevId, currentId);
    }

}
