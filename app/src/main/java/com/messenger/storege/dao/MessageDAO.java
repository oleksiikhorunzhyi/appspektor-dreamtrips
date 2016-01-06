package com.messenger.storege.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Message$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.util.RxContentResolver;

import rx.Observable;
import rx.schedulers.Schedulers;

public class MessageDAO extends BaseDAO {

    public MessageDAO(Context context) {
        super(context);
    }

    public Observable<Cursor> getMessage(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT m.*, u." + User$Table.USERNAME + " as " + User$Table.USERNAME +
                        ", u." + User$Table.USERAVATARURL + " as " + User$Table.USERAVATARURL +
                        ", u." + User$Table.SOCIALID + " as " + User$Table.SOCIALID +

                        " FROM " + Message.TABLE_NAME + " m LEFT JOIN " + User$Table.TABLE_NAME + " u" +
                        " ON m." + Message.COLUMN_FROM + " = u." + User$Table._ID +
                        " WHERE " + Message.COLUMN_CONVERSATION_ID + " = ?" +
                        " ORDER BY " + Message.COLUMN_DATE)
                .withSelectionArgs(new String[]{conversationId}).build();

        return query(q, Message.CONTENT_URI, User.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io());
    }

    public Observable<Integer> markMessagesAsRead(String conversationId, long visibleTime) {
        return Observable.<Integer>create(subscriber -> {
            subscriber.onStart();
            String clause = Message.COLUMN_CONVERSATION_ID + " = ? "
                    + "AND " + Message.COLUMN_DATE + " <= ? "
                    + "AND " + Message.COLUMN_READ + " = ? ";
            ContentValues cv = new ContentValues(1);
            cv.put(Message$Table.READ, true);
            try {
                subscriber.onNext(getContentResolver().update(Message.CONTENT_URI, cv, clause,
                        new String[]{conversationId, String.valueOf(visibleTime), String.valueOf(0)}));
            } catch (Exception e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest();
    }

    public Observable<Integer> unreadCount(String conversationId, long firstMessageTime) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT COUNT(_id) FROM " + Message.TABLE_NAME + " " +
                        "WHERE " + Message.COLUMN_CONVERSATION_ID + " = ? "
                        + "AND " + Message.COLUMN_DATE + " > ? "
                        + "AND " + Message.COLUMN_READ + " = ? ")
                .withSelectionArgs(new String[]{conversationId, String.valueOf(firstMessageTime)})
                .build();

        return query(q)
                .map(cursor -> cursor.moveToFirst() ? cursor.getInt(0) : 0);
    }
}
