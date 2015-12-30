package com.messenger.storege.utils;

import android.content.Context;
import android.database.Cursor;

import com.messenger.messengerservers.entities.User;
import com.messenger.util.RxContentResolver;

import rx.Observable;

public class UsersDAO extends BaseDAO {

    public UsersDAO(Context context) {
        super(context);
    }

    public Observable<Cursor> getFriends() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users WHERE " + User.COLUMN_ID + "=?")
                .withSelectionArgs(new String[]{String.valueOf(1)})
                .withSortOrder("ORDER BY " + User.COLUMN_NAME + " COLLATE NOCASE ASC")
                .build();
        return query(q, User.CONTENT_URI);
    }
}
