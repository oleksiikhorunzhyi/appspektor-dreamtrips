package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataParticipant$Adapter;
import com.messenger.entities.DataParticipant$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Adapter;
import com.messenger.entities.DataUser$Table;
import com.messenger.util.RxContentResolver;

import java.util.ArrayList;
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

        return query(q, DataUser.CONTENT_URI)
                .subscribeOn(Schedulers.io())
                .compose(DaoTransformers.toEntity(DataUser.class));
    }

    public Observable<List<Pair<DataUser, String>>> getParticipants(String conversationId) {
        final String affiliation = "p." + DataParticipant$Table.AFFILIATION + " as " + DataParticipant$Table.AFFILIATION;
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection(participantsSelection("*, " + affiliation))
                .withSelectionArgs(new String[]{conversationId})
                .withSortOrder(userOrder())
                .build();

        return query(q, DataUser.CONTENT_URI, DataParticipant.CONTENT_URI)
                .map(this::convertToListUserWithAffiliation);
    }

    private List<Pair<DataUser, String>> convertToListUserWithAffiliation(Cursor cursor) {
        final DataUser$Adapter adapter = new DataUser$Adapter();
        final int affiliationColumn = cursor.getColumnIndex(DataParticipant$Table.AFFILIATION);
        List<Pair<DataUser, String>> result = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            DataUser user = adapter.loadFromCursor(cursor);
            result.add(new Pair<>(user, cursor.getString(affiliationColumn)));
        }
        cursor.close();
        return result;
    }

    public Observable<List<DataUser>> getParticipantsEntities(String conversationId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection(participantsSelection("*"))
                .withSelectionArgs(new String[]{conversationId})
                .withSortOrder(userOrder())
                .build();

        return query(q, DataUser.CONTENT_URI, DataParticipant.CONTENT_URI)
                .compose(DaoTransformers.toEntityList(DataUser.class));
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
        return "ORDER BY " + DataUser$Table.FIRSTNAME + ", " + DataUser$Table.LASTNAME + " COLLATE NOCASE ASC";
    }

    public void delete(String conversationId, String userId) {
        getContentResolver().delete(DataParticipant.CONTENT_URI,
                DataParticipant$Table.CONVERSATIONID + "=? AND " +
                        DataParticipant$Table.USERID + "=?", new String[]{conversationId, userId});
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
