package com.messenger.messengerservers.entities;

import android.net.Uri;
import android.support.annotation.StringDef;

import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
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
    public static final String COLUMN_ID = "_id";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column String _id;
    @Column String ownerId;
    @Column String subject;
    @Column String type;
    @Column int unreadMessageCount;

    public Conversation() {}

    public Conversation(String id, String subject, String type) {
        setId(id);
        setSubject(subject);
        setType(type);
    }

    private Conversation(Builder builder) {
        setId(builder.id);
        setOwnerId(builder.ownerId);
        setSubject(builder.subject);
        setType(builder.type);
        setUnreadMessageCount(builder.unreadMessageCount);
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conversation that = (Conversation) o;

        return _id != null ? _id.equals(that._id) : that._id == null;

    }

    @Override
    public int hashCode() {
        return _id != null ? _id.hashCode() : 0;
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
        private String ownerId;
        private String subject;
        private String type;
        private int unreadMessageCount = 0;

        public Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder ownerId(String id) {
            ownerId = id;
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

        public Builder unreadMessageCount(int unreadMessageCount){
            this.unreadMessageCount = unreadMessageCount;
            return this;
        }

        public Conversation build() {
            return new Conversation(this);
        }
    }
}
