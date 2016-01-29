package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.User;
import com.messenger.entities.User$Adapter;
import com.messenger.entities.User$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.ConditionQueryBuilder;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import java.util.List;

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

    public void deleteFriends() {
        new Delete().from(User.class).where(Condition.column(User$Table.FRIEND).is(true)).queryClose();
    }

    public void markUserAsFriend(List<String> ids, boolean isFriend) {
        if (ids.isEmpty()) return;
        //
        String first = ids.get(0);
        String[] other = Queryable.from(ids).skip(1).toArray(String.class);

        new Update<>(User.class)
                .set(Condition.column(User$Table.FRIEND).eq(isFriend))
                .where(new ConditionQueryBuilder<>(User.class, Condition.column(User$Table._ID).in(first, other)))
                .queryClose();
        getContext().getContentResolver().notifyChange(User.CONTENT_URI, null);
    }

    public void save(List<User> users) {
        bulkInsert(users, new User$Adapter(), User.CONTENT_URI);
    }
}
