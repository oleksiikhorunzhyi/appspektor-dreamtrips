package com.messenger.messengerservers.entities;

import android.net.Uri;

import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@Table(databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
@TableEndpoint(name = ParticipantsRelationship.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
public class ParticipantsRelationship extends BaseProviderModel<ParticipantsRelationship> {
    public static final String TABLE_NAME = "ParticipantsRelationship";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column() String id;

    @Column String conversationId;
    @Column String userId;

    public ParticipantsRelationship(String conversationId, User user) {
        this(conversationId, user.getId());
    }

    public ParticipantsRelationship(String conversationId, String userId) {
        this.id = String.format("%s_%s", conversationId, userId);
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public ParticipantsRelationship() {
    }

    public String getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getUserId() {
        return userId;
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
}
