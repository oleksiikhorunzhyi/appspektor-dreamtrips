package com.messenger.storege.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Message$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.worldventures.dreamtrips.modules.trips.model.Schedule;

import rx.Observable;
import rx.schedulers.Schedulers;

public class MessageDAO extends BaseDAO {

    public MessageDAO(Context context) {
        super(context);
    }

    public Observable<Cursor> getMessage(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT m.*, u." + User.COLUMN_NAME + " as " + User.COLUMN_NAME +
                        ", u." + User.COLUMN_AVATAR + " as " + User.COLUMN_AVATAR +
                        ", u." + User.COLUMN_SOCIAL_ID + " as " + User.COLUMN_SOCIAL_ID +

                        " FROM " + Message.TABLE_NAME + " m LEFT JOIN " + User.TABLE_NAME + " u" +
                        " ON m." + Message.COLUMN_FROM + " = u." + User.COLUMN_ID +
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
                .withSelectionArgs(new String[] {conversationId, String.valueOf(firstMessageTime)})
                .build();

        return query(q)
                .map(cursor -> cursor.moveToFirst() ? cursor.getInt(0) : 0);
    }
}
