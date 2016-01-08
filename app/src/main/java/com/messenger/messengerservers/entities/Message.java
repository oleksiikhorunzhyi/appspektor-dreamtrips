package com.messenger.messengerservers.entities;

import android.net.Uri;
import android.support.annotation.IntDef;

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
import java.util.Locale;

@TableEndpoint(name = Message.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = Message.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class Message extends BaseProviderModel<Message> {
    public static final String TABLE_NAME = "Messages";

    @Deprecated public static final String COLUMN_DATE = "date";
    @Deprecated public static final String COLUMN_TEXT = "text";
    @Deprecated public static final String COLUMN_FROM = "fromId";
    @Deprecated public static final String COLUMN_CONVERSATION_ID = "conversationId";
    @Deprecated public static final String COLUMN_READ = "read";
    @Deprecated public static final String _ID = "_id";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column String _id;
    @Column String fromId;
    @Column String toId;
    @Column String text;
    @Column Date date;
    @Column String conversationId;
    @Deprecated @Column boolean read;
    @Status.MessageStatus @Column int status;

    private Locale locale;

    public Message() {
    }

    public Message(String from, String to, String text, String id) {
        this.fromId = from;
        this.toId = to;
        this.text = text;
        this._id = id;
    }

    private Message(Builder builder) {
        _id = builder.id;
        setConversationId(builder.conversationId);
        setFromId(builder.from);
        setToId(builder.to);
        setText(builder.text);
        setDate(builder.date);
        setLocale(builder.locale);
        setStatus(builder.status);
        setRead(builder.read);
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

    public String getId() {
        return _id;
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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Deprecated
    public boolean isRead() {
        return read;
    }

    @Deprecated
    public void setRead(boolean read) {
        this.read = read;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(@Status.MessageStatus int status) {
        this.status = status;
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

    public static final class Builder {
        private String id;
        private String conversationId;
        private String from;
        private String to;
        private String text;
        private Date date;
        private Locale locale;
        private int status;
        @Deprecated
        private boolean read;

        public Builder() {
        }

        public Builder id(String val){
            this.id = val;
            return this;
        }

        public Builder conversationId(String conversationId){
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

        public Builder locale(Locale val) {
            locale = val;
            return this;
        }

        public Builder status(@Status.MessageStatus int val) {
            status = val;
            return this;
        }

        @Deprecated
        public Builder read(boolean val) {
            read = val;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    public static class Status {
        public static final int ERROR = -1;
        public static final int SENDING = 0;
        public static final int SENT = 1;
        public static final int READ = 2;

        @IntDef({ERROR, SENDING, SENT, READ})
        @interface MessageStatus {}
    }
}
