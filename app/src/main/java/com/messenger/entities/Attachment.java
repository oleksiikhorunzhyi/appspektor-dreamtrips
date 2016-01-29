package com.messenger.entities;

import android.provider.BaseColumns;
import android.support.annotation.StringDef;

import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(tableName = Attachment.TABLE_NAME, databaseName = MessengerDatabase.NAME)
public class Attachment extends BaseModel {
    public static final String TABLE_NAME = "Attachments";

    @PrimaryKey(autoincrement = true)
    @Column(name = BaseColumns._ID)
    long id;

    @Column
    String messageId;

    @Column
    String kind;

    @Column
    String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Kind.AttachmentKind
    public String getKind() {
        return kind;
    }

    public void setKind(@Kind.AttachmentKind String kind) {
        this.kind = kind;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Kind {
        public static final String PICTURE = "picture";
        public static final String LOCATION = "location";

        @StringDef({PICTURE, LOCATION})
        public @interface AttachmentKind {}
    }
}
