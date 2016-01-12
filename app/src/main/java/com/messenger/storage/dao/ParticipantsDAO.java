package com.messenger.storage.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.ParticipantsRelationship$Table;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ParticipantsDAO extends BaseDAO {

    @Deprecated
    public ParticipantsDAO(Context context) {
        super(context);
    }

    public ParticipantsDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    @Deprecated
    public static Observable<Cursor> selectParticipants(RxContentResolver contentResolver, String conversationId, Uri... observeUris) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                        "JOIN ParticipantsRelationship p " +
                        "ON p.userId = u._id " +
                        "WHERE p.conversationId = ?")
                .withSelectionArgs(new String[]{conversationId})
                .withSortOrder(userOrder())
                .build();

        return contentResolver.query(q, observeUris)
                .compose(new DaoTransformer());
    }

    public Observable<User> getParticipant(String conversationId, String yourId) {
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
                .map(cursor -> {
                    User res = SqlUtils.convertToModel(false, User.class, cursor);
                    cursor.close();
                    return res;
                });
    }

    public Observable<Cursor> getParticipants(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection(participantsSelection("*"))
                .withSelectionArgs(new String[]{conversationId})
                .withSortOrder(userOrder())
                .build();

        return query(q, User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .compose(new DaoTransformer())
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io());
    }

    public Observable<Cursor> getNewParticipantsCandidates(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users " +
                        "where (" + User$Table._ID + " not in (" + participantsSelection(User$Table._ID) + "))" +
                        "and (" + User$Table.FRIEND + " = 1)"
                )
                .withSelectionArgs(new String[]{conversationId})
                .withSortOrder(userOrder())
                .build();

        return query(q, User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .compose(new DaoTransformer())
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    private static String participantsSelection(String projection) {
        return "SELECT " + projection + " FROM Users u " +
                "JOIN ParticipantsRelationship p " +
                "ON p.userId = u._id " +
                "WHERE p.conversationId = ?";
    }

    @NonNull
    private static String userOrder() {
        return "ORDER BY " + User$Table.USERNAME + " COLLATE NOCASE ASC";
    }

    public void delete(String conversationId, String userId) {
        getContentResolver().delete(ParticipantsRelationship.CONTENT_URI,
                ParticipantsRelationship$Table.CONVERSATIONID + "=? AND " +
                        ParticipantsRelationship$Table.USERID + "=?", new String[]{conversationId, userId});
    }

    public void delete(String conversationId) {
        getContentResolver().delete(ParticipantsRelationship.CONTENT_URI,
                ParticipantsRelationship$Table.CONVERSATIONID + "=?", new String[]{conversationId});
    }

    @Deprecated
    public static void delete(ContentResolver resolver, String conversationId, String userId) {
        resolver.delete(ParticipantsRelationship.CONTENT_URI,
                ParticipantsRelationship$Table.CONVERSATIONID + "=? AND " +
                        ParticipantsRelationship$Table.USERID + "=?", new String[]{conversationId, userId});
    }
}
