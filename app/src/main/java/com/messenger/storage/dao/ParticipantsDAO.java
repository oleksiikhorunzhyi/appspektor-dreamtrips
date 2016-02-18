package com.messenger.storage.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataParticipant$Adapter;
import com.messenger.entities.DataParticipant$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ParticipantsDAO extends BaseDAO {

    public ParticipantsDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    public Observable<DataUser> getParticipant(String conversationId, String yourId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                        "JOIN " + DataParticipant.TABLE_NAME + " p " +
                        "ON p.userId = u._id " +
                        "WHERE p.conversationId = ? AND u._id<>?")
                .withSelectionArgs(new String[]{conversationId, yourId})
                .build();

        return query(q)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .map(cursor -> {
                    DataUser res = SqlUtils.convertToModel(false, DataUser.class, cursor);
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

        return query(q, DataUser.CONTENT_URI, DataParticipant.CONTENT_URI);
    }

    public Observable<List<DataUser>> getParticipantsEntities(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection(participantsSelection("*"))
                .withSelectionArgs(new String[]{conversationId})
                .withSortOrder(userOrder())
                .build();

        return query(q, DataUser.CONTENT_URI, DataParticipant.CONTENT_URI)
                .map(cursor -> {
                    List<DataUser> users = SqlUtils.convertToList(DataUser.class, cursor);
                    cursor.close();
                    return users;
                });
    }

    public Observable<Cursor> getNewParticipantsCandidates(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users " +
                        "where (" + DataUser$Table._ID + " not in (" + participantsSelection(DataUser$Table._ID) + "))" +
                        "and (" + DataUser$Table.FRIEND + " = 1)"
                )
                .withSelectionArgs(new String[]{conversationId})
                .withSortOrder(userOrder())
                .build();

        return query(q, DataUser.CONTENT_URI, DataParticipant.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    private static String participantsSelection(String projection) {
        return "SELECT " + projection + " FROM Users u " +
                "JOIN " + DataParticipant.TABLE_NAME + " p " +
                "ON p.userId = u._id " +
                "WHERE p.conversationId = ?";
    }

    @NonNull
    private static String userOrder() {
        return "ORDER BY " + DataUser$Table.USERNAME + " COLLATE NOCASE ASC";
    }

    public void delete(String conversationId, String userId) {
        getContentResolver().delete(DataParticipant.CONTENT_URI,
                DataParticipant$Table.CONVERSATIONID + "=? AND " +
                        DataParticipant$Table.USERID + "=?", new String[]{conversationId, userId});
    }

    public void delete(String conversationId) {
        getContentResolver().delete(DataParticipant.CONTENT_URI,
                DataParticipant$Table.CONVERSATIONID + "=?", new String[]{conversationId});
    }

    @Deprecated
    public static void delete(ContentResolver resolver, String conversationId, String userId) {
        resolver.delete(DataParticipant.CONTENT_URI,
                DataParticipant$Table.CONVERSATIONID + "=? AND " +
                        DataParticipant$Table.USERID + "=?", new String[]{conversationId, userId});
    }

    public void save(DataParticipant participant) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(participant));
    }

    public void save(List<DataParticipant> participants) {
        bulkInsert(participants, new DataParticipant$Adapter(), DataParticipant.CONTENT_URI);
    }

    public void deleteBySyncTime(long time) {
        getContentResolver().delete(DataParticipant.CONTENT_URI,
                DataParticipant$Table.SYNCTIME + " < " + "?" +
                        " AND " + DataParticipant$Table.SYNCTIME + " NOT IN " +
                        "(SELECT " + DataConversation$Table.TABLE_NAME + "." + DataConversation$Table.SYNCTIME +
                        " FROM " + DataConversation$Table.TABLE_NAME +
                        " WHERE " + DataConversation$Table.TABLE_NAME + "." + DataConversation$Table._ID + " = " +
                        DataParticipant$Table.TABLE_NAME + "." + DataParticipant$Table.CONVERSATIONID + ")",
                new String[]{String.valueOf(time)});
    }
}
