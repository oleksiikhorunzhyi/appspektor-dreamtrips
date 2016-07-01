package com.messenger.entities;

import android.net.Uri;
import android.provider.BaseColumns;

import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

import java.util.Date;

@TableEndpoint(name = DataMessage.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = DataMessage.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class DataMessage extends BaseProviderModel<DataMessage> {
    public static final String TABLE_NAME = "Messages";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column(name = BaseColumns._ID) String id;
    @Column String fromId;
    @Column String toId;
    @Column String text;
    @Column String locale;
    @Column Date date;
    @Column String conversationId;
    @MessageStatus.Status @Column int status;
    @MessageType.Type @Column String type;
    @Column long syncTime;

    public DataMessage() {
    }

    public DataMessage(Message message) {
        setId(message.getId());
        setConversationId(message.getConversationId());
        setFromId(message.getFromId());
        setToId(message.getToId());
        setStatus(message.getStatus());
        setDate(new Date(message.getDate()));
        setType(message.getType());

        MessageBody body = message.getMessageBody();
        if (body != null) {
            setText(body.getText());
            setLocale(body.getLocale());
        }
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocaleName(){
        return locale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(@MessageStatus.Status int status) {
        this.status = status;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Uri getDeleteUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return CONTENT_URI;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return id + "  " + text;
    }

    public static final class Builder {
        private DataMessage message;

        public Builder() {
            message = new DataMessage();
        }

        public Builder id(String id) {
            message.setId(id);
            return this;
        }

        public Builder conversationId(String conversationId) {
            message.setConversationId(conversationId);
            return this;
        }

        public Builder from(String fromId) {
            message.setFromId(fromId);
            return this;
        }

        public Builder to(String toId) {
            message.setToId(toId);
            return this;
        }

        public Builder text(String text) {
            message.setText(text);
            return this;
        }

        public Builder date(Date date) {
            message.setDate(date);
            return this;
        }

        public Builder locale(String locale) {
            message.setLocale(locale);
            return this;
        }

        public Builder status(@MessageStatus.Status int status) {
            message.setStatus(status);
            return this;
        }

        public Builder syncTime(long syncTime) {
            message.setSyncTime(syncTime);
            return this;
        }

        public Builder type(@MessageType.Type String type) {
            message.setType(type);
            return this;
        }

        public DataMessage build() {
            return message;
        }
    }

    public Message toChatMessage() {
        return new Message.Builder()
                .fromId(fromId)
                .toId(toId)
                .id(id)
                .conversationId(conversationId)
                .status(status)
                .type(type)
                .build();
    }
}
