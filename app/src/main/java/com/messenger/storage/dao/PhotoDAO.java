package com.messenger.storage.dao;

import android.content.Context;
import android.net.Uri;

import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataPhotoAttachment$Adapter;
import com.messenger.entities.DataPhotoAttachment$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.List;

import rx.Observable;

public class PhotoDAO extends BaseAttachmentDAO<DataPhotoAttachment> {

    public PhotoDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    @Override
    protected ModelAdapter<DataPhotoAttachment> getModelAdapter() {
        return new DataPhotoAttachment$Adapter();
    }

    @Override
    protected Uri getModelTableUri() {
        return DataPhotoAttachment.CONTENT_URI;
    }

    @Override
    protected String getModelTableName() {
        return DataPhotoAttachment.TABLE_NAME;
    }

    @Override
    protected String getIDColumnName() {
        return DataPhotoAttachment$Table._ID;
    }

    public Observable<List<DataPhotoAttachment>> getErrorAttachments() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * " +
                        "FROM " + DataPhotoAttachment.TABLE_NAME + " as p " +
                        "LEFT JOIN " + DataAttachment$Table.TABLE_NAME + " a " +
                        "ON a." + DataAttachment$Table._ID + " = p." + DataPhotoAttachment$Table._ID + " " +
                        "JOIN " + DataMessage$Table.TABLE_NAME + " m " +
                        "ON a." + DataAttachment$Table.MESSAGEID + " = m." + DataMessage$Table._ID + " " +

                        "WHERE m." + DataMessage$Table.STATUS + "= ? ")
                .withSelectionArgs(new String[]{Integer.toString(MessageStatus.ERROR)}).build();

        return query(q, DataPhotoAttachment.CONTENT_URI, DataMessage.CONTENT_URI).first()
                .compose(DaoTransformers.toAttachments(DataPhotoAttachment.class));
    }

    public Observable<List<DataPhotoAttachment>> getPendingAttachments(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT p.* " +
                        "FROM " + DataPhotoAttachment.TABLE_NAME + " p " +
                        "LEFT JOIN " + DataAttachment$Table.TABLE_NAME + " a " +
                        "ON a." + DataAttachment$Table._ID + "= p." + DataPhotoAttachment$Table._ID + " " +
                        "WHERE a." + DataAttachment$Table.CONVERSATIONID + " = ? " +
                        "AND p." + DataPhotoAttachment$Table.UPLOADTASKID + " <> 0")
                .withSelectionArgs(new String[]{conversationId}).build();
        return query(q, DataPhotoAttachment.CONTENT_URI)
                .compose(DaoTransformers.toAttachments(DataPhotoAttachment.class));
    }

}
