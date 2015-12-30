package com.messenger.storege.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.ParticipantsRelationship$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ParticipantsDAO extends BaseDAO {

    public ParticipantsDAO(Context context) {
        super(context);
    }

    @Deprecated
    public static void delete(ContentResolver resolver, String conversationId, String userId) {
        resolver.delete(ParticipantsRelationship.CONTENT_URI,
                ParticipantsRelationship$Table.CONVERSATIONID + "=? AND " +
                        ParticipantsRelationship$Table.USERID + "=?", new String[]{conversationId, userId});
    }

    @Deprecated
    public static Observable<Cursor> selectParticipants(RxContentResolver contentResolver, String conversationId, Uri... observeUris) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                        "JOIN ParticipantsRelationship p " +
                        "ON p.userId = u._id " +
                        "WHERE p.conversationId = ?")
                .withSortOrder("ORDER BY " + User.COLUMN_NAME + " COLLATE NOCASE ASC")
                .withSelectionArgs(new String[]{conversationId}).build();

        return contentResolver.query(q, observeUris)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Cursor> getParticipants(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                                "JOIN ParticipantsRelationship p " +
                                "ON p.userId = u._id " +
                                "WHERE p.conversationId = ?" +
                                "ORDER BY " + User.COLUMN_NAME + " COLLATE NOCASE ASC"
                ).withSelectionArgs(new String[]{conversationId}).build();

        return query(q, User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io());
    }

    public void delete(String conversationId, String userId) {
        getContentResolver().delete(ParticipantsRelationship.CONTENT_URI,
                ParticipantsRelationship$Table.CONVERSATIONID + "=? AND " +
                        ParticipantsRelationship$Table.USERID + "=?", new String[]{conversationId, userId});
    }

    public Observable<User> getMate(String conversationId, String yourId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                        "JOIN ParticipantsRelationship p " +
                        "ON p.userId = u._id " +
                        "WHERE p.conversationId = ? AND u._id<>?")
                .withSelectionArgs(new String[]{conversationId, yourId})
                .build();

        return query(q)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .map(cursor -> SqlUtils.convertToModel(false, User.class, cursor));
    }
}
