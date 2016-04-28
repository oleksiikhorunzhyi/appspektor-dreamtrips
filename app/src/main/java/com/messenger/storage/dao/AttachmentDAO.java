package com.messenger.storage.dao;

import android.content.Context;
import android.net.Uri;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import rx.Observable;

public class AttachmentDAO extends BaseAttachmentDAO<DataAttachment> {

    public AttachmentDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    @Override
    protected ModelAdapter<DataAttachment> getModelAdapter() {
        return new DataAttachment$Adapter();
    }

    @Override
    protected Uri getModelTableUri() {
        return DataAttachment.CONTENT_URI;
    }

    @Override
    protected String getModelTableName() {
        return DataAttachment.TABLE_NAME;
    }

    @Override
    protected String getIDColumnName() {
        return DataAttachment$Table._ID;
    }

    public Observable<DataAttachment> getAttachmentById(String attachmentId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataAttachment.TABLE_NAME + " " +
                        "WHERE " + DataAttachment$Table._ID + "=?")
                .withSelectionArgs(new String[]{attachmentId})
                .build();
        return query(q, DataAttachment.CONTENT_URI)
                .compose(DaoTransformers.toEntity(DataAttachment.class));
    }

    public Observable<DataAttachment> getAttachmentByMessageId(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataAttachment.TABLE_NAME + " " +
                        "WHERE " + DataAttachment$Table.MESSAGEID + "=?")
                .withSelectionArgs(new String[]{messageId})
                .build();
        return query(q, DataAttachment.CONTENT_URI)
                .compose(DaoTransformers.toEntity(DataAttachment.class));
    }

}
