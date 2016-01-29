package com.messenger.storage.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Message$Adapter;
import com.messenger.messengerservers.entities.Message$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
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
                .withSelection("SELECT m.*, u." + User$Table.USERNAME + " as " + User$Table.USERNAME +
                        ", u." + User$Table.USERAVATARURL + " as " + User$Table.USERAVATARURL +
                        ", u." + User$Table.SOCIALID + " as " + User$Table.SOCIALID +

                        " FROM " + Message.TABLE_NAME + " m LEFT JOIN " + User$Table.TABLE_NAME + " u" +
                        " ON m." + Message$Table.FROMID + " = u." + User$Table._ID +
                        " WHERE " + Message$Table.CONVERSATIONID + " = ?" +
                        " ORDER BY " + Message$Table.DATE)
                .withSelectionArgs(new String[]{conversationId}).build();

        return query(q, Message.CONTENT_URI, User.CONTENT_URI);
    }

    public Observable<Message> getMessage(String messageId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + Message$Table.TABLE_NAME + " " +
                "WHERE " + Message$Table._ID + "=?")
                .withSelectionArgs(new String[] {messageId})
                .build();

        return query(q, Message.CONTENT_URI)
                .map(cursor -> {
                    Message res = SqlUtils.convertToModel(false, Message.class, cursor);
                    cursor.close();
                    return res;
                });
    }
    public Observable<Integer> markMessagesAsRead(String conversationId, String userId, long visibleTime) {
        return Observable.create(subscriber -> {
            subscriber.onStart();
            String clause = Message$Table.CONVERSATIONID + " = ? "
                    + "AND " + Message$Table.DATE + " <= ? "
                    + "AND " + Message$Table.FROMID + " <> ? "
                    + "AND " + Message$Table.STATUS + " = ? ";
            ContentValues cv = new ContentValues(1);
            cv.put(Message$Table.STATUS, Message.Status.READ);
            try {
                subscriber.onNext(getContentResolver().update(Message.CONTENT_URI, cv, clause,
                        new String[]{conversationId, String.valueOf(visibleTime),
                                userId, String.valueOf(Message.Status.SENT)})
                );
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<Integer> unreadCount(String conversationId, String userId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT COUNT(_id) FROM " + Message.TABLE_NAME + " " +
                        "WHERE " + Message$Table.CONVERSATIONID + " = ? "
                        + "AND " + Message$Table.FROMID + " <> ? "
                        + "AND " + Message$Table.STATUS + " = ? ")
                .withSelectionArgs(new String[]{conversationId, userId, String.valueOf(Message.Status.SENT)})
                .build();

        return query(q, Message.CONTENT_URI)
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
                .withSelection("SELECT COUNT(_id) FROM " + Message.TABLE_NAME + " " +
                        "WHERE " + Message$Table.CONVERSATIONID + " = ? "
                        + "AND " + Message$Table.DATE + " >=  " +
                        "( SELECT MIN(" + Message$Table.DATE + ") FROM " + Message.TABLE_NAME + " "
                        + "WHERE " + Message$Table.FROMID + " <> ? "
                        + "AND " + Message$Table.STATUS + " = ? )")
                .withSelectionArgs(new String[]{conversationId, userId, String.valueOf(Message.Status.SENT)})
                .build();

        return query(q, Message.CONTENT_URI)
                .map(cursor -> {
                    int res = cursor.moveToFirst() ? cursor.getInt(0) : 0;
                    cursor.close();
                    return res;
                });
    }

    public void save(List<Message> messages) {
        bulkInsert(messages, new Message$Adapter(), Message.CONTENT_URI);
    }
}
