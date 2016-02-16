package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;

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
        save(Collections.singletonList(attachment));
    }

    public Observable<DataAttachment> getAttachmentByMessageId(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataAttachment.TABLE_NAME + " " +
                        "WHERE " + DataAttachment$Table.MESSAGEID + "=?")
                .withSelectionArgs(new String[] {messageId})
                .build();
        return query(q, DataAttachment.CONTENT_URI)
                .map(cursor -> {
                    DataAttachment dataAttachment = SqlUtils.convertToModel(false, DataAttachment.class, cursor);
                    cursor.close();
                    return dataAttachment;
                });

    }

    public Observable<Cursor> getPendingAttachments(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT a.* " +
                        "FROM " + DataAttachment.TABLE_NAME + " a " +

                        "WHERE " + DataAttachment$Table.CONVERSATIONID + "=? " +
                        "AND " + DataAttachment$Table.UPLOADTASKID + "<>0")
                .withSelectionArgs(new String[]{conversationId}).build();

        return query(q, DataAttachment.CONTENT_URI);
    }

}
