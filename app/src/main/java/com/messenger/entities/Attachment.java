package com.messenger.entities;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.ImageAttachment;
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

import java.util.List;

@Table(tableName = Attachment.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
@TableEndpoint(name = Attachment.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
public class Attachment extends BaseProviderModel<Attachment> {
    public static final String TABLE_NAME = "Attachments";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column(name = BaseColumns._ID) String id;
    @Column String messageId;
    @Column String type;
    @Column String url;

    public Attachment() {
    }

    protected Attachment(@NonNull AttachmentHolder attachment, String messageId, int index) {
        this.id = createId(messageId, index);
        this.messageId = messageId;
        String type = this.type = attachment.getType();

        switch (type) {
            case AttachmentType.IMAGE: {
                ImageAttachment image = (ImageAttachment) attachment.getItem();
                url = image.getUrl();
            }
        }
        attachment.getItem();
    }

    private String createId(String messageId, int index) {
        return String.format("%s__%s", messageId, index);
    }

    @Nullable
    public static List<Attachment> fromMessage(@NonNull com.messenger.messengerservers.model.Message message) {
        MessageBody body = message.getMessageBody();
        List<AttachmentHolder> attachmentHolders;
        if (body == null || (attachmentHolders = body.getAttachments()) == null || attachmentHolders.isEmpty()) return null;

        return Queryable.from(attachmentHolders)
                .map((elem, idx) -> new Attachment(elem, message.getId(), idx))
                .toList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @AttachmentType.Type
    public String getType() {
        return type;
    }

    public void setType(@AttachmentType.Type String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
