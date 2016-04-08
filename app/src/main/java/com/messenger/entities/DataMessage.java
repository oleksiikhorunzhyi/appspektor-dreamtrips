package com.messenger.entities;

import android.net.Uri;
import android.provider.BaseColumns;

import com.messenger.messengerservers.constant.MessageStatus;
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

    public static final int MESSAGE_FORMAT_VERSION = 1;

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
    @Column long syncTime;
    @Column int version = MESSAGE_FORMAT_VERSION;

    public DataMessage() {
    }

    public DataMessage(Message message) {
        setId(message.getId());
        setConversationId(message.getConversationId());
        setFromId(message.getFromId());
        setToId(message.getToId());
        setStatus(message.getStatus());
        setDate(new Date(message.getDate()));

        MessageBody body = message.getMessageBody();
        if (body != null) {
            setText(body.getText());
            setVersion(body.getVersion());
            setLocale(body.getLocale());
        }
    }

    private DataMessage(Builder builder) {
        setId(builder.id);
        setConversationId(builder.conversationId);
        setFromId(builder.from);
        setToId(builder.to);
        setText(builder.text);
        setDate(builder.date);
        setSyncTime(builder.syncTime);
        setStatus(builder.status);
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocaleName(){
        return locale;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
        private String id;
        private String conversationId;
        private String from;
        private String to;
        private String text;
        private Date date;
        private String locale;
        private int status;
        private long syncTime;

        public Builder() {
        }

        public Builder id(String val) {
            this.id = val;
            return this;
        }

        public Builder conversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder from(String val) {
            from = val;
            return this;
        }

        public Builder to(String val) {
            to = val;
            return this;
        }

        public Builder text(String val) {
            text = val;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder locale(String val) {
            locale = val;
            return this;
        }

        public Builder status(@MessageStatus.Status int val) {
            status = val;
            return this;
        }

        public Builder syncTime(long val) {
            syncTime = val;
            return this;
        }

        public DataMessage build() {
            return new DataMessage(this);
        }
    }

    public Message toChatMessage() {
        return new Message.Builder()
                .fromId(fromId)
                .toId(toId)
                .id(id)
                .status(status)
                .build();
    }
}
