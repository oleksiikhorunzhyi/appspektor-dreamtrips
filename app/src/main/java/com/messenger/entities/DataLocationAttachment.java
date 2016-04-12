package com.messenger.entities;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.LocationAttachment;
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

@Table(tableName = DataLocationAttachment.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
@TableEndpoint(name = DataLocationAttachment.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
public class DataLocationAttachment extends BaseProviderModel<DataAttachment> {
    public static final String TABLE_NAME = "Locations";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey
    @Column(name = BaseColumns._ID)
    String id;
    @Column
    double lat;
    @Column
    double lng;

    public DataLocationAttachment() {
    }

    private DataLocationAttachment(Builder builder) {
        setId(builder.id);
        setCoordinates(builder.lat, builder.lng);
    }

    public DataLocationAttachment(@NonNull LocationAttachment attachment, Message message, int index) {
        this.id = createId(message.getId(), index);
        this.lat = attachment.getLat();
        this.lng = attachment.getLng();
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

    public void setCoordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
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
    public static List<DataLocationAttachment> fromMessage(@NonNull Message message) {
        MessageBody body = message.getMessageBody();
        List<AttachmentHolder> attachmentHolders;
        if (body == null || (attachmentHolders = body.getAttachments()) == null || attachmentHolders.isEmpty()) {
            return Collections.emptyList();
        }

        return Queryable.from(attachmentHolders)
                .filter(attachmentHolder -> attachmentHolder != null)
                .filter(attachmentHolder -> TextUtils.equals(attachmentHolder.getType(), AttachmentType.LOCATION))
                .map((elem, idx) -> new DataLocationAttachment((LocationAttachment) elem.getItem(), message, idx))
                .toList();
    }

    public static final class Builder {
        String id;
        double lat;
        double lng;

        public Builder() {
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder coordinates(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
            return this;
        }

        public DataLocationAttachment build() {
            return new DataLocationAttachment(this);
        }
    }
}
