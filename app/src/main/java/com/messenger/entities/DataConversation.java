package com.messenger.entities;

import android.net.Uri;

import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@TableEndpoint(name = DataConversation.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = DataConversation.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class DataConversation extends BaseProviderModel<DataConversation> {
    public static final String TABLE_NAME = "Conversations";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column String _id;
    @Column String ownerId;
    @Column String subject;
    @Column String avatar;
    @ConversationType.Type @Column String type;
    @Column String status;
    @Column int unreadMessageCount;
    @Column long syncTime;
    @Column long lastActiveDate;

    public DataConversation() {}

    public DataConversation(Conversation conversation) {
        setId(conversation.getId());
        setOwnerId(conversation.getOwnerId());
        setSubject(conversation.getSubject());
        setAvatar(conversation.getAvatar());
        setStatus(conversation.getStatus());
        setType(conversation.getType());
        setUnreadMessageCount(conversation.getUnreadMessageCount());
        setLastActiveDate(conversation.getLastActiveDate());
    }

    private DataConversation(Builder builder) {
        setId(builder.id);
        setOwnerId(builder.ownerId);
        setSubject(builder.subject);
        setAvatar(builder.avatar);
        setStatus(builder.status);
        setType(builder.type);
        setUnreadMessageCount(builder.unreadMessageCount);
        setLastActiveDate(builder.date);
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @ConversationType.Type
    public String getType() {
        return type;
    }

    public void setType(@ConversationType.Type String type) {
        this.type = type;
    }

    @ConversationStatus.Status
    public String getStatus() {
        return status;
    }

    public void setStatus(@ConversationStatus.Status String status){
        this.status = status;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    public long getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(long lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataConversation that = (DataConversation) o;

        return _id != null ? _id.equals(that._id) : that._id == null;

    }

    @Override
    public String toString() {
        return "Conversation{" +
                "_id='" + _id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", subject='" + subject + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", unreadMessageCount=" + unreadMessageCount +
                '}';
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

    public static final class Builder {
        private String id;
        private String ownerId;
        private String subject;
        private String avatar;
        private String type;
        private String status;
        private long date;
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

        public Builder lastActiveDate(long dateMS) {
            date = dateMS;
            return this;
        }

        public Builder subject(String val) {
            subject = val;
            return this;
        }

        public Builder avatar(String val) {
            avatar = val;
            return this;
        }

        public Builder type(@ConversationType.Type String val) {
            type = val;
            return this;
        }

        public Builder status(@ConversationStatus.Status String val) {
            status = val;
            return this;
        }

        public DataConversation build() {
            return new DataConversation(this);
        }
    }
}
