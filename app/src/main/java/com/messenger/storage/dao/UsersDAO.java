package com.messenger.storage.dao;

import android.content.Context;

import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Adapter;
import com.messenger.entities.DataUser$Table;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class UsersDAO extends BaseDAO {

    public UsersDAO(RxContentResolver rxContentResolver, Context context) {
        super(context, rxContentResolver);
    }

    @Deprecated
    public static DataUser getUser(String userId) {
        return new Select().from(DataUser.class).byIds(userId).querySingle();
    }

    public Observable<List<DataUser>> getExitingUserByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Observable.from(Collections.emptyList());
        //
        StringBuilder sb = new StringBuilder("(");
        for (String id: ids) {
            sb.append(String.format(" '%s',", id));
        }
        sb.setCharAt(sb.length() - 1, ')');

        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * " +
                        " FROM " + DataUser.TABLE_NAME +
                        " WHERE " + DataUser$Table._ID + " in " + sb.toString())
                        .build();
        return query(q, DataUser.CONTENT_URI)
                .compose(DaoTransformers.toEntityList(DataUser.class));
    }

    public Observable<DataUser> getUserById(String id) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataUser.TABLE_NAME + " WHERE " + DataUser$Table._ID + "=?")
                .withSelectionArgs(new String[]{String.valueOf(id)})
                .build();
        return query(q, DataUser.CONTENT_URI)
                .compose(DaoTransformers.toEntity(DataUser.class));
    }

    public Observable<List<DataUser>> getFriends(String currentUserId) {
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM " + DataUser.TABLE_NAME + " " +
                        "WHERE " + DataUser$Table._ID +  "<>?" + " AND " + DataUser$Table.FRIEND + "=?")
                .withSelectionArgs(new String[]{currentUserId, String.valueOf(1)})
                .withSortOrder("ORDER BY " + DataUser$Table.FIRSTNAME + ", " + DataUser$Table.LASTNAME + " COLLATE NOCASE ASC")
                .build();
        return query(q, DataUser.CONTENT_URI)
                .compose(DaoTransformers.toEntityList(DataUser.class));
    }

    public void unfriendAll() {
        new Update<>(DataUser.class).set(Condition.column(DataUser$Table.FRIEND).is(false)).queryClose();
    }

    public void save(List<DataUser> users) {
        bulkInsert(users, new DataUser$Adapter(), DataUser.CONTENT_URI);
    }

    public void save(DataUser user) {
        // BaseProviderModel.save() saves all null strings as "null"(https://github.com/Raizlabs/DBFlow/pull/430)
        save(Collections.singletonList(user));
    }
}
