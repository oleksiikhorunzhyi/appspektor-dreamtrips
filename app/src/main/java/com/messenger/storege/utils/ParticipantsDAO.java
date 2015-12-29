package com.messenger.storege.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.util.RxContentResolver;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ParticipantsDAO {

    public static void delete(ContentResolver resolver, String conversationId, String userId) {
        resolver.delete(ParticipantsRelationship.CONTENT_URI,
                ParticipantsRelationship.COLUMN_CONVERSATION_ID + "=? AND " +
                        ParticipantsRelationship.COLUMN_USER_ID + "=?", new String[] {conversationId, userId});
    }

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
}
