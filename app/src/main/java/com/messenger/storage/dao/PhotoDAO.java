package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataPhotoAttachment$Adapter;
import com.messenger.entities.DataPhotoAttachment$Table;
import com.messenger.entities.DataPhotoAttachment.PhotoAttachmentStatus;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.MessengerDatabase;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
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
        return DataPhotoAttachment$Table.PHOTOATTACHMENTID;
    }

    public Observable<List<DataPhotoAttachment>> getPhotoAttachments(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT p.* " +
                        "FROM " + DataPhotoAttachment.TABLE_NAME + " p " +
                        "INNER JOIN " + DataAttachment$Table.TABLE_NAME + " a " +
                        "ON a." + DataAttachment$Table._ID + "=p." + DataPhotoAttachment$Table.PHOTOATTACHMENTID + " " +
                        "INNER JOIN " + DataMessage$Table.TABLE_NAME + " m " +
                        "ON a." + DataAttachment$Table.MESSAGEID + "=m." + DataMessage$Table._ID + " " +

                        "WHERE m." + DataMessage$Table.CONVERSATIONID + "=?")
                .withSelectionArgs(new String[]{conversationId}).build();

        return query(q, DataPhotoAttachment.CONTENT_URI)
                .compose(DaoTransformers.toEntityList(DataPhotoAttachment.class));
    }

    public Observable<List<DataPhotoAttachment>> getErrorAttachments() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * " +
                        "FROM " + DataPhotoAttachment.TABLE_NAME + " as p " +
                        "LEFT JOIN " + DataAttachment$Table.TABLE_NAME + " a " +
                        "ON a." + DataAttachment$Table._ID + " = p." + DataPhotoAttachment$Table.PHOTOATTACHMENTID + " " +
                        "JOIN " + DataMessage$Table.TABLE_NAME + " m " +
                        "ON a." + DataAttachment$Table.MESSAGEID + " = m." + DataMessage$Table._ID + " " +

                        "WHERE m." + DataMessage$Table.STATUS + "= ? ")
                .withSelectionArgs(new String[]{Integer.toString(MessageStatus.ERROR)}).build();

        return query(q, DataPhotoAttachment.CONTENT_URI, DataMessage.CONTENT_URI).first()
                .compose(DaoTransformers.toEntityList(DataPhotoAttachment.class));
    }

    @Override
    protected <T extends BaseModel> void bulkInsert(List<T> collection, ModelAdapter<T> adapter, Uri uri) {
        SQLiteDatabase db = FlowManager.getDatabase(MessengerDatabase.NAME).getWritableDatabase();

        for (T t : collection) {
            DataPhotoAttachment attachment = (DataPhotoAttachment) t;
            ContentValues values = toContentValues(attachment);
            int updated = db.update(adapter.getTableName(), values,
                    DataPhotoAttachment$Table.PHOTOATTACHMENTID + "=?", new String[] {attachment.getPhotoAttachmentId()});
            if (updated == 0) {
                db.insertWithOnConflict(adapter.getTableName(), null, values,
                        ConflictAction.getSQLiteDatabaseAlgorithmInt(adapter.getInsertOnConflictAction()));
            }
        }
        getContentResolver().notifyChange(uri, null);
    }

    private ContentValues toContentValues(DataPhotoAttachment model) {
        ContentValues contentValues = new ContentValues();
        if (model.getPhotoAttachmentId() != null)  {
            contentValues.put(DataPhotoAttachment$Table.PHOTOATTACHMENTID, model.getPhotoAttachmentId());
        }
        if (model.getUrl() != null)  {
            contentValues.put(DataPhotoAttachment$Table.URL, model.getUrl());
        }
        if (model.getLocalPath() != null)  {
            contentValues.put(DataPhotoAttachment$Table.LOCALPATH, model.getLocalPath());
        }
        contentValues.put(DataPhotoAttachment$Table.UPLOADSTATE, model.getUploadState());
        return contentValues;
    }

    public int markSendingAsFailed() {
        ContentValues cv = new ContentValues();
        cv.put(DataPhotoAttachment$Table.UPLOADSTATE, PhotoAttachmentStatus.FAILED);
        return getContentResolver().update(DataPhotoAttachment.CONTENT_URI, cv,
                DataPhotoAttachment$Table.UPLOADSTATE + "=?",
                new String[] {String.valueOf(PhotoAttachmentStatus.UPLOADING)}
        );
    }
}
