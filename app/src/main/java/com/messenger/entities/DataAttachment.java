package com.messenger.entities;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.model.AttachmentHolder;
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

@Table(tableName = DataAttachment.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
@TableEndpoint(name = DataAttachment.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
public class DataAttachment extends BaseProviderModel<DataAttachment> {
    public static final String TABLE_NAME = "Attachments";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey
    @Column(name = BaseColumns._ID)
    String id;
    @Column
    String messageId;
    @Column
    String conversationId;
    @AttachmentType.Type
    @Column
    String type;

    public DataAttachment() {
    }

    private DataAttachment(Builder builder) {
        setId(createId(builder.messageId, 0));
        setConversationId(builder.conversationId);
        setMessageId(builder.messageId);
        setType(builder.type);
    }

    public DataAttachment(@NonNull AttachmentHolder attachment, Message message, int index) {
        this.id = createId(message.getId(), index);
        this.messageId = message.getId();
        this.conversationId = message.getConversationId();
        //
        this.type = attachment.getType();
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    @AttachmentType.Type
    public String getType() {
        return type;
    }

    public void setType(@AttachmentType.Type String type) {
        this.type = type;
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
    public static List<DataAttachment> fromMessage(@NonNull Message message) {
        MessageBody body = message.getMessageBody();
        List<AttachmentHolder> attachmentHolders;
        if (body == null || (attachmentHolders = body.getAttachments()) == null || attachmentHolders.isEmpty()) {
            return Collections.emptyList();
        }

        return Queryable.from(attachmentHolders)
                .filter(attachmentHolder -> attachmentHolder != null)
                .map((elem, idx) -> new DataAttachment(elem, message, idx))
                .toList();
    }

    public static final class Builder {
        String messageId;
        String conversationId;
        String type;

        public Builder() {
        }

        public Builder conversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder messageId(String val) {
            messageId = val;
            return this;
        }

        public Builder type(@AttachmentType.Type String val) {
            type = val;
            return this;
        }

        public DataAttachment build() {
            return new DataAttachment(this);
        }
    }
}
