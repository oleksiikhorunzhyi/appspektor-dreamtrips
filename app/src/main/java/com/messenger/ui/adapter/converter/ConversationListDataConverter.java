package com.messenger.ui.adapter.converter;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.storage.dao.ConversationsDAO;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import org.immutables.value.Value;

import java.util.Date;

public class ConversationListDataConverter {

    public Result convert(Cursor cursor) {
        return ImmutableResult.builder()
                .conversation(toConversation(cursor))
                .message(toMessage(cursor))
                .translation(toTranslation(cursor))
                .sender(toMessageSender(cursor))
                .recipient(toMessageRecipient(cursor))
                .attachmentType(toAttachmentType(cursor))
                .build();
    }

    public DataConversation toConversation(Cursor cursor) {
        return SqlUtils.convertToModel(true, DataConversation.class, cursor);
    }

    public DataMessage toMessage(Cursor cursor) {
        DataMessage message = new DataMessage();
        message.setText(cursor.getString(cursor.getColumnIndex(DataMessage$Table.TEXT)));
        message.setFromId(cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID)));
        message.setToId(cursor.getString(cursor.getColumnIndex(DataMessage$Table.TOID)));
        message.setDate(new Date(cursor.getInt(cursor.getColumnIndex(DataMessage$Table.DATE))));
        message.setType(cursor.getString(cursor.getColumnIndex(ConversationsDAO.MESSAGE_TYPE_COLUMN)));
        return message;
    }

    public DataTranslation toTranslation(Cursor cursor) {
        return SqlUtils.convertToModel(true, DataTranslation.class, cursor);
    }

    public DataUser toMessageSender(Cursor cursor) {
        DataUser user = new DataUser(cursor.getString(cursor.getColumnIndex(ConversationsDAO.SENDER_ID_COLUMN)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(ConversationsDAO.SENDER_FIRST_NAME_COLUMN)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(ConversationsDAO.SENDER_LAST_NAME_COLUMN)));
        return user;
    }

    public DataUser toMessageRecipient(Cursor cursor) {
        DataUser user = new DataUser(cursor.getString(cursor.getColumnIndex(ConversationsDAO.RECIPIENT_ID_COLUMN)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(ConversationsDAO.RECIPIENT_FIRST_NAME_COLUMN)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(ConversationsDAO.RECIPIENT_LAST_NAME_COLUMN)));
        return user;
    }

    public String toAttachmentType(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(ConversationsDAO.ATTACHMENT_TYPE_COLUMN));
    }

    @Value.Immutable
    public interface Result {
        DataConversation getConversation();
        @Nullable DataMessage getMessage();
        @Nullable String getAttachmentType();
        @Nullable DataTranslation getTranslation();
        DataUser getSender();
        @Nullable DataUser getRecipient();
    }
}