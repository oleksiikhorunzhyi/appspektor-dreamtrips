package com.messenger.entities;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.ImageAttachment;
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

import java.util.Collections;
import java.util.List;

@Table(tableName = DataPhotoAttachment.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
@TableEndpoint(name = DataPhotoAttachment.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
public class DataPhotoAttachment extends BaseProviderModel<DataAttachment> {
    public static final String TABLE_NAME = "Photos";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey
    @Column(name = BaseColumns._ID)
    String id;
    @Column
    String url;
    @Column
    int uploadTaskId;

    public DataPhotoAttachment() {
    }

    private DataPhotoAttachment(Builder builder) {
        setId(builder.id);
        setUrl(builder.url);
        setUploadTaskId(builder.amazonTaskId);
    }

    public DataPhotoAttachment(@NonNull ImageAttachment attachment, Message message, int index) {
        this.id = createId(message.getId(), index);
        url = attachment.getOriginUrl();
    }

    private String createId(String messageId, int index) {
        return String.format("%s__%s", messageId, index);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUploadTaskId(int amazonTaskId) {
        this.uploadTaskId = amazonTaskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUploadTaskId() {
        return uploadTaskId;
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

    @NonNull
    public static List<DataPhotoAttachment> fromMessage(@NonNull Message message) {
        MessageBody body = message.getMessageBody();
        List<AttachmentHolder> attachmentHolders;
        if (body == null || (attachmentHolders = body.getAttachments()) == null || attachmentHolders.isEmpty()) {
            return Collections.emptyList();
        }

        return Queryable.from(attachmentHolders)
                .filter(attachmentHolder -> attachmentHolder != null)
                .map((elem, idx) -> new DataPhotoAttachment((ImageAttachment)elem.getItem(), message, idx))
                .toList();
    }

    public static final class Builder {
        String id;
        String url;
        int amazonTaskId;

        public Builder() {
        }

        public Builder id(String var) {
            id = var;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public DataPhotoAttachment build() {
            return new DataPhotoAttachment(this);
        }
    }
}
