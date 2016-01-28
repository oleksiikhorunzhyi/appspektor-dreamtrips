package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;

import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Adapter;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class UsersDAO extends BaseDAO {

    @Deprecated
    public UsersDAO(Context context) {
        super(context);
    }

    public UsersDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    @Deprecated
    public static User getUser(String userId) {
        return new Select().from(User.class).byIds(userId).querySingle();
    }

    public Observable<User> getUserById(String id) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users WHERE " + User$Table._ID + "=?")
                .withSelectionArgs(new String[]{String.valueOf(id)})
                .build();
        return query(q, User.CONTENT_URI)
                .map(c -> {
                    User user = SqlUtils.convertToModel(false, User.class, c);
                    c.close();
                    return user;
                });
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

    public void save(List<User> friends) {
        bulkInsert(friends, new User$Adapter(), User.CONTENT_URI);
    }

    public void deleteFriends() {
        new Delete().from(User.class).where(Condition.column(User$Table.FRIEND).is(true)).queryClose();
    }
}
