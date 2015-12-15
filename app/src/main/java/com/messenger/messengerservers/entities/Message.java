package com.messenger.messengerservers.entities;

import android.net.Uri;

import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

import java.util.Date;
import java.util.Locale;

@ModelContainer
@TableEndpoint(name = Message.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = Message.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class Message extends BaseProviderModel<Message> {
    static final String TABLE_NAME = "Messages";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column String _id;
    @ForeignKey(
            references = {@ForeignKeyReference(
                    columnName = "fromId",
                    columnType = String.class,
                    foreignColumnName = "_id")},
            saveForeignKeyModel = true)
    @Column User from;
    @ForeignKey(
            references = {@ForeignKeyReference(
                    columnName = "toId",
                    columnType = String.class,
                    foreignColumnName = "_id")},
            saveForeignKeyModel = true)
    @Column User to;
    @Column String text;
    @Column Date date;
    @Column String conversationId;

    private Locale locale;

    public Message() {
    }

    public Message(User from, User to, String text, String id) {
        this.from = from;
        this.to = to;
        this.text = text;
        this._id = id;
    }

    private Message(Builder builder) {
        _id = builder.id;
        setConversationId(builder.conversationId);
        setFrom(builder.from);
        setTo(builder.to);
        setText(builder.text);
        setDate(builder.date);
        setLocale(builder.locale);
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
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
        private User from;
        private User to;
        private String text;
        private Date date;
        private Locale locale;

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

        public Builder from(User val) {
            from = val;
            return this;
        }

        public Builder to(User val) {
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

        public Message build() {
            return new Message(this);
        }
    }
}
