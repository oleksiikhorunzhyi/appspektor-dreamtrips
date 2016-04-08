package com.messenger.entities;

import android.net.Uri;
import android.provider.BaseColumns;

import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@TableEndpoint(name = DataTranslation.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = DataTranslation.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class DataTranslation extends BaseProviderModel<DataTranslation> {
    public static final String TABLE_NAME = "Translations";

    @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME)
    public static final Uri CONTENT_URI = MessengerDatabase.buildUri(TABLE_NAME);

    @PrimaryKey
    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @Column(name = BaseColumns._ID) String id;
    @Column String translation;
    @TranslationStatus.Status @Column() int translateStatus;

    public DataTranslation() {
    }

    public DataTranslation(String id, String translation, @TranslationStatus.Status int translateStatus) {
        this.id = id;
        this.translation = translation;
        this.translateStatus = translateStatus;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @TranslationStatus.Status
    public int getTranslateStatus() {
        return translateStatus;
    }

    public void setTranslateStatus(@TranslationStatus.Status int translateStatus) {
        this.translateStatus = translateStatus;
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
