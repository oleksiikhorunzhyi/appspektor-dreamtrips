package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Adapter;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataPhotoAttachment$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataTranslation$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class MessageDAO extends BaseDAO {

    public static final String ATTACHMENT_ID = DataAttachment$Table.TABLE_NAME + DataAttachment$Table._ID;
    public static final String TRANSLATION_ID = DataTranslation$Table.TABLE_NAME + DataTranslation$Table._ID;


    public MessageDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    public Observable<DataMessage> getMessage(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataMessage$Table.TABLE_NAME + " " +
                        "WHERE " + DataMessage$Table._ID + "=?")
                .withSelectionArgs(new String[]{messageId})
                .build();

        return query(q, DataMessage.CONTENT_URI)
                .compose(DaoTransformers.toDataMessage());
    }

    public Observable<DataMessage> getMessageByAttachmentId(String attachmentId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT m.* " +
                        "FROM " + DataMessage$Table.TABLE_NAME + " m " +
                        "LEFT JOIN " + DataAttachment$Table.TABLE_NAME + " a " +
                        "ON a." + DataAttachment$Table.MESSAGEID + "= m." + DataMessage$Table._ID + " " +
                        "WHERE a." + DataAttachment$Table._ID + "=?")
                .withSelectionArgs(new String[]{attachmentId})
                .build();

        return query(q, DataMessage.CONTENT_URI)
                .compose(DaoTransformers.toDataMessage());
    }

    public Observable<Cursor> getMessagesBySyncTime(String conversationId, long syncTime) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT m.*, " +
                        "u." + DataUser$Table.FIRSTNAME + " as " + DataUser$Table.FIRSTNAME + ", " +
                        "u." + DataUser$Table.LASTNAME + " as " + DataUser$Table.LASTNAME + ", " +
                        "u." + DataUser$Table.USERAVATARURL + " as " + DataUser$Table.USERAVATARURL + ", " +
                        "u." + DataUser$Table.SOCIALID + " as " + DataUser$Table.SOCIALID + ", " +

                        "a." + DataAttachment$Table._ID + " as " + ATTACHMENT_ID + ", " +
                        "a." + DataAttachment$Table.TYPE + " as " + DataAttachment$Table.TYPE + ", " +

                        "p." + DataPhotoAttachment$Table.URL + " as " + DataPhotoAttachment$Table.URL + ", " +

                        "t." + DataTranslation$Table._ID + " as " + TRANSLATION_ID + ", " +
                        "t." + DataTranslation$Table.TRANSLATION + " as " + DataTranslation$Table.TRANSLATION + ", "+
                        "t." + DataTranslation$Table.TRANSLATESTATUS + " as " + DataTranslation$Table.TRANSLATESTATUS + " "+

                        "FROM " + DataMessage.TABLE_NAME + " m " +
                        "LEFT JOIN " + DataUser$Table.TABLE_NAME + " u " +
                        "ON m." + DataMessage$Table.FROMID + "=u." + DataUser$Table._ID + " " +
                        "LEFT JOIN " + DataAttachment.TABLE_NAME + " a " +
                        "ON m." + DataMessage$Table._ID + "=a." + DataAttachment$Table.MESSAGEID + " " +
                        "LEFT JOIN " + DataPhotoAttachment.TABLE_NAME + " p " +
                        "ON a." + DataAttachment$Table._ID + "=p." + DataPhotoAttachment$Table._ID + " " +
                        "LEFT JOIN " + DataTranslation.TABLE_NAME + " t " +
                        "ON m." + DataMessage$Table._ID + "=t." + DataTranslation$Table._ID + " " +

                        "WHERE m." + DataMessage$Table.CONVERSATIONID + "=? " +
                        "AND m." + DataMessage$Table.SYNCTIME + " >=? " +
                        "ORDER BY m." + DataMessage$Table.DATE)
                .withSelectionArgs(new String[]{conversationId, Long.toString(syncTime)}).build();

        return query(q, DataMessage.CONTENT_URI, DataUser.CONTENT_URI, DataAttachment.CONTENT_URI, DataTranslation.CONTENT_URI);
    }

    public Observable<DataMessage> findNewestUnreadMessage(String conversationId, String currentUserId, long syncTime) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataMessage$Table.TABLE_NAME +
                        " WHERE " + DataMessage$Table.CONVERSATIONID + " =?" +
                        " AND " + DataMessage$Table.FROMID + " <>?" +
                        " AND " + DataMessage$Table.SYNCTIME + " >=?" +
                        " ABD " + DataMessage$Table.STATUS + "<>" + MessageStatus.READ +
                        " ORDER BY " + DataMessage$Table.DATE + " DESC " +
                        " LIMIT 1")
                .withSelectionArgs(new String[]{conversationId, currentUserId,
                        Long.toString(syncTime)})
                .build();

        return query(q, DataMessage.CONTENT_URI)
                .compose(DaoTransformers.toDataMessage());
    }

    public Observable<Integer> markMessagesAsRead(String conversationId, String userId, long visibleTime) {
        return Observable.create(subscriber -> {
            subscriber.onStart();
            String clause = DataMessage$Table.CONVERSATIONID + " = ? "
                    + "AND " + DataMessage$Table.DATE + " <= ? "
                    + "AND " + DataMessage$Table.FROMID + " <> ? "
                    + "AND " + DataMessage$Table.STATUS + " = ? ";
            ContentValues cv = new ContentValues(1);
            cv.put(DataMessage$Table.STATUS, MessageStatus.READ);
            try {
                subscriber.onNext(getContentResolver().update(DataMessage.CONTENT_URI, cv, clause,
                                new String[]{conversationId, String.valueOf(visibleTime),
                                        userId, String.valueOf(MessageStatus.SENT)})
                );
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<Integer> unreadCount(String conversationId, String userId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT COUNT(_id) FROM " + DataMessage.TABLE_NAME + " " +
                        "WHERE " + DataMessage$Table.CONVERSATIONID + " = ? "
                        + "AND " + DataMessage$Table.FROMID + " <> ? "
                        + "AND " + DataMessage$Table.STATUS + " = ? ")
                .withSelectionArgs(new String[]{conversationId, userId, String.valueOf(MessageStatus.SENT)})
                .build();

        return query(q, DataMessage.CONTENT_URI)
                .map(cursor -> {
                    int res = cursor.moveToFirst() ? cursor.getInt(0) : 0;
                    cursor.close();
                    return res;
                });
    }

    public void save(List<DataMessage> messages) {
        bulkInsert(messages, new DataMessage$Adapter(), DataMessage.CONTENT_URI);
    }

    public void save(DataMessage message) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(message));
    }

    public void deleteMessageById(String messageId) {
        new Delete().from(DataMessage.class).byIds(messageId).query();
    }

    public void updateStatus(String msgId, int messageStatus, long time) {
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(DataMessage$Table.STATUS, messageStatus);
        contentValues.put(DataMessage$Table.DATE, time);
        contentValues.put(DataMessage$Table.SYNCTIME, time);
        getContentResolver().update(DataMessage.CONTENT_URI, contentValues, DataMessage$Table._ID + "=?", new String[]{msgId});
    }
}
