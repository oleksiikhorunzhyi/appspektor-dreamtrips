package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Adapter;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import java.util.List;

import rx.Observable;

public class MessageDAO extends BaseDAO {

    @Deprecated
    public MessageDAO(Context context) {
        super(context);
    }

    public MessageDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    public Observable<Cursor> getMessages(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT m.*, " +
                        "u." + DataUser$Table.USERNAME + " as " + DataUser$Table.USERNAME + ", " +
                        "u." + DataUser$Table.USERAVATARURL + " as " + DataUser$Table.USERAVATARURL + ", " +
                        "u." + DataUser$Table.SOCIALID + " as " + DataUser$Table.SOCIALID + ", " +

                        "a." +  DataAttachment$Table.TYPE + " as " + DataAttachment$Table.TYPE + ", " +
                        "a." + DataAttachment$Table.URL + " as " + DataAttachment$Table.URL + " " +

                        "FROM " + DataMessage.TABLE_NAME + " m " +
                        "LEFT JOIN " + DataUser$Table.TABLE_NAME + " u " +
                        "ON m." + DataMessage$Table.FROMID + "=u." + DataUser$Table._ID + " " +
                        "LEFT JOIN " + DataAttachment.TABLE_NAME + " a " +
                        "ON m." + DataMessage$Table._ID + "=a." + DataAttachment$Table.MESSAGEID + " " +

                        "WHERE m." + DataMessage$Table.CONVERSATIONID + "=? " +
                        "ORDER BY m." + DataMessage$Table.DATE)
                .withSelectionArgs(new String[]{conversationId}).build();

        return query(q, DataMessage.CONTENT_URI, DataUser.CONTENT_URI, DataAttachment.CONTENT_URI);
    }

    public Observable<DataMessage> getMessage(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataMessage$Table.TABLE_NAME + " " +
                "WHERE " + DataMessage$Table._ID + "=?")
                .withSelectionArgs(new String[] {messageId})
                .build();

        return query(q, DataMessage.CONTENT_URI)
                .map(cursor -> {
                    DataMessage res = SqlUtils.convertToModel(false, DataMessage.class, cursor);
                    cursor.close();
                    return res;
                });
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

    /**
     *
     * @return observable, which emits int values which includes first unread message
     */
    public Observable<Integer> countFromFirstUnreadMessage(String conversationId, String userId){
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT COUNT(_id) FROM " + DataMessage.TABLE_NAME + " " +
                        "WHERE " + DataMessage$Table.CONVERSATIONID + " = ? "
                        + "AND " + DataMessage$Table.DATE + " >=  " +
                        "( SELECT MIN(" + DataMessage$Table.DATE + ") FROM " + DataMessage.TABLE_NAME + " "
                        + "WHERE " + DataMessage$Table.FROMID + " <> ? "
                        + "AND " + DataMessage$Table.STATUS + " = ? )")
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
        message.save();
    }

    public void updateStatus(String msgId, int messageStatus, long time) {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(DataMessage$Table.STATUS, messageStatus);
        contentValues.put(DataMessage$Table.DATE, time);
        getContentResolver().update(DataMessage.CONTENT_URI, contentValues, DataMessage$Table._ID +"=?", new String[] {msgId});
    }
}
