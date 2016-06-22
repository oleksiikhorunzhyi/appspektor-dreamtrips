package com.messenger.entities;

import android.net.Uri;

import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@TableEndpoint(name = DataTyping.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = DataTyping.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class DataTyping extends BaseProviderModel<DataTyping> {
    public static final String TABLE_NAME = "TypingUsers";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @PrimaryKey
    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @Column String typingId;
    @Column String conversationId;
    @Column String userId;

    public DataTyping() {}

    public DataTyping(String conversationId, String userId) {
        this.typingId = generateId(conversationId, userId);
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public static String generateId(String conversationId, String userId) {
        return String.format("%s_%s", conversationId, userId);
    }

    public String getTypingId() {
        return typingId;
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
