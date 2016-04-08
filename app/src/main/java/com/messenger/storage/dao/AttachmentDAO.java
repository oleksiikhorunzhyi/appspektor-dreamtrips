package com.messenger.storage.dao;

import android.content.Context;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.util.RxContentResolver;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class AttachmentDAO extends BaseDAO {

    public AttachmentDAO(Context context, RxContentResolver rxContentResolver) {
        super(context, rxContentResolver);
    }

    public void save(List<DataAttachment> attachments) {
        bulkInsert(attachments, new DataAttachment$Adapter(), DataAttachment.CONTENT_URI);
    }

    public void save(DataAttachment attachment) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(attachment));
    }

    public Observable<DataAttachment> getAttachmentById(String attachmentId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataAttachment.TABLE_NAME + " " +
                        "WHERE " + DataAttachment$Table._ID + "=?")
                .withSelectionArgs(new String[]{attachmentId})
                .build();
        return query(q, DataAttachment.CONTENT_URI)
                .compose(DaoTransformers.toDataAttachment());
    }

    public Observable<DataAttachment> getAttachmentByMessageId(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataAttachment.TABLE_NAME + " " +
                        "WHERE " + DataAttachment$Table.MESSAGEID + "=?")
                .withSelectionArgs(new String[]{messageId})
                .build();
        return query(q, DataAttachment.CONTENT_URI)
                .compose(DaoTransformers.toDataAttachment());

    }

    public Observable<List<DataAttachment>> getErrorAtachments() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * " +
                        "FROM " + DataAttachment.TABLE_NAME + " as a " +
                        "JOIN " + DataMessage$Table.TABLE_NAME + " m " +
                        "ON a." + DataAttachment$Table.MESSAGEID + " = m." + DataMessage$Table._ID + " " +

                        "WHERE m." + DataMessage$Table.STATUS + "= ? ")
                .withSelectionArgs(new String[]{Integer.toString(MessageStatus.ERROR)}).build();

        return query(q, DataAttachment.CONTENT_URI, DataMessage.CONTENT_URI).first()
                .compose(DaoTransformers.toDataAttachments());
    }

    public Observable<List<DataAttachment>> getPendingAttachments(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * " +
                        "FROM " + DataAttachment.TABLE_NAME + " " +

                        "WHERE " + DataAttachment$Table.CONVERSATIONID + " = ? " +
                        "AND " + DataAttachment$Table.UPLOADTASKID + " <> 0")
                .withSelectionArgs(new String[]{conversationId}).build();

        return query(q, DataAttachment.CONTENT_URI)
                .compose(DaoTransformers.toDataAttachments());
    }

    public void deleteAttachment(DataAttachment dataAttachment) {
        dataAttachment.delete();
    }

}
