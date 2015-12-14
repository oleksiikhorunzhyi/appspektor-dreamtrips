package com.messenger.messengerservers.entities;

import android.net.Uri;
import android.support.annotation.StringDef;


import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@TableEndpoint(name = Conversation.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = Conversation.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class Conversation extends BaseProviderModel<Conversation> {
    public static final String TABLE_NAME = "Conversations";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column String id;
    @Column String subject;
    @Column String type;

    @ForeignKey(
            references = {@ForeignKeyReference(
                    columnName = "lastMessageId",
                    columnType = String.class,
                    foreignColumnName = Message._ID)},
            saveForeignKeyModel = false)
    @Column Message lastMessage;

    public Conversation() {
    }

    public Conversation(String id, String subject, String type) {
        setId(id);
        setSubject(subject);
        setType(type);
    }

    private Conversation(Builder builder) {
        setId(builder.id);
        setSubject(builder.subject);
        setType(builder.type);
        setLastMessage(builder.lastMessage);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Type.ConversationType
    public String getType() {
        return type;
    }

    public void setType(@Type.ConversationType String type) {
        this.type = type;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
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

    public static final class Type {
        public static final String CHAT = "chat";
        public static final String GROUP = "group";
        public static final String RINK = "rink";
        public static final String RANK = "rank";

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({CHAT, GROUP, RINK, RANK})
        public @interface ConversationType {
        }
    }

    public static final class Builder {
        private String id;
        private String subject;
        private String type;
        private Message lastMessage;

        public Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder subject(String val) {
            subject = val;
            return this;
        }

        public Builder type(@Type.ConversationType String val) {
            type = val;
            return this;
        }

        public Builder lastMessage(Message val) {
            lastMessage = val;
            return this;
        }

        public Conversation build() {
            return new Conversation(this);
        }
    }
}
