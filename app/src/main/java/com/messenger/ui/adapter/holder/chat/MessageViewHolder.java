package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.ChatCellDelegate;
import com.messenger.ui.adapter.holder.CursorViewHolder;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class MessageViewHolder extends CursorViewHolder {

    protected String currentUserId;
    protected DataMessage dataMessage;
    protected DataUser dataUserSender;
    protected String conversationType;

    @InjectView(R.id.chat_date)
    public TextView dateTextView;
    @InjectView(R.id.message_container)
    public View messageContainer;

    protected boolean selected;
    protected boolean isOwnMessage;
    protected boolean needMarkUnreadMessage;

    protected ChatCellDelegate cellDelegate;

    public MessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        dataMessage = SqlUtils.convertToModel(true, DataMessage.class, cursor);
        dataUserSender = convertToUserModel(cursor);
        conversationType =  cursor.getString(cursor.getColumnIndex(MessageDAO.CONVERSATION_TYPE));
    }

    public DataUser convertToUserModel(Cursor cursor) {
        // we cannot use SqlUtils either for message or for sender
        // as user ID collides with message ID and is replaced by alias
        DataUser user = new DataUser(cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(DataUser$Table.FIRSTNAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(DataUser$Table.LASTNAME)));
        user.setAvatarUrl(cursor.getString(cursor.getColumnIndex(DataUser$Table.USERAVATARURL)));
        user.setSocialId(cursor.getInt(cursor.getColumnIndex(DataUser$Table.SOCIALID)));
        return user;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setCellDelegate(ChatCellDelegate cellDelegate) {
        this.cellDelegate = cellDelegate;
    }

    public void setNeedMarkUnreadMessage(boolean needMarkUnreadMessage) {
        this.needMarkUnreadMessage = needMarkUnreadMessage;
    }

    public void setOwnMessage(boolean ownMessage) {
        isOwnMessage = ownMessage;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public View getTimestampClickableView() {
        return messageContainer;
    }
}
