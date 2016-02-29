package com.messenger.storage.dao;

import android.content.Context;
import android.database.Cursor;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Adapter;
import com.messenger.entities.DataUser$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.ConditionQueryBuilder;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class UsersDAO extends BaseDAO {
    public static final String USER_DISPLAY_NAME = "displayName";

    public UsersDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    @Deprecated
    public static DataUser getUser(String userId) {
        return new Select().from(DataUser.class).byIds(userId).querySingle();
    }

    public Observable<DataUser> getUserById(String id) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataUser.TABLE_NAME + " WHERE " + DataUser$Table._ID + "=?")
                .withSelectionArgs(new String[]{String.valueOf(id)})
                .build();
        return query(q, DataUser.CONTENT_URI)
                .map(c -> {
                    DataUser user = SqlUtils.convertToModel(false, DataUser.class, c);
                    c.close();
                    return user;
                });
    }

    public Observable<Cursor> getFriends() {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT *, " + DataUser$Table.FIRSTNAME + "|| ' ' ||" +  DataUser$Table.LASTNAME + " as " + USER_DISPLAY_NAME + " " +
                        "FROM " + DataUser.TABLE_NAME + " WHERE " + DataUser$Table.FRIEND + "=?")
                .withSelectionArgs(new String[]{String.valueOf(1)})
                .withSortOrder("ORDER BY " + DataUser$Table.FIRSTNAME + ", " + DataUser$Table.LASTNAME + " COLLATE NOCASE ASC")
                .build();
        return query(q, DataUser.CONTENT_URI)
                .subscribeOn(Schedulers.io());
    }

    public void deleteFriends() {
        new Update<>(DataUser.class).set(Condition.column(DataUser$Table.FRIEND).is(true)).queryClose();
    }

    public void markUserAsFriend(List<String> ids, boolean isFriend) {
        if (ids.isEmpty()) return;
        //
        String first = ids.get(0);
        String[] other = Queryable.from(ids).skip(1).toArray(String.class);

        new Update<>(DataUser.class)
                .set(Condition.column(DataUser$Table.FRIEND).eq(isFriend))
                .where(new ConditionQueryBuilder<>(DataUser.class, Condition.column(DataUser$Table._ID).in(first, other)))
                .queryClose();
        getContext().getContentResolver().notifyChange(DataUser.CONTENT_URI, null);
    }

    public void save(List<DataUser> users) {
        bulkInsert(users, new DataUser$Adapter(), DataUser.CONTENT_URI);
    }

    public void save(DataUser user) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(user));
    }
}
