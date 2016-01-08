package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;

import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import rx.Observable;
import rx.schedulers.Schedulers;

public class UsersDAO extends BaseDAO {

    public UsersDAO(Context context) {
        super(context);
    }

    @Deprecated
    public static User getUser(String userId) {
        return new Select().from(User.class).byIds(userId).querySingle();
    }

    public Observable<User> getUserById(String id) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users WHERE " + User$Table._ID + "=?")
                .withSelectionArgs(new String[]{String.valueOf(id)})
                .withSortOrder("ORDER BY " + User$Table.USERNAME + " COLLATE NOCASE ASC")
                .build();
        return query(q, User.CONTENT_URI)
                .map(c -> SqlUtils.convertToModel(false, User.class, c));
    }

    public Observable<Cursor> getFriends() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users WHERE " + User$Table.FRIEND + "=?")
                .withSelectionArgs(new String[]{String.valueOf(1)})
                .withSortOrder("ORDER BY " + User$Table.USERNAME + " COLLATE NOCASE ASC")
                .build();
        return query(q, User.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io());
    }
}
