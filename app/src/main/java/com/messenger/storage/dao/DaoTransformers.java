package com.messenger.storage.dao;

import android.database.Cursor;

import com.messenger.entities.DataUser;
import com.raizlabs.android.dbflow.sql.SqlUtils;

import java.util.List;

import rx.Observable;

public class DaoTransformers {
    public static Observable.Transformer<Cursor, List<DataUser>> toDataUsers() {
        return cursorObservable -> cursorObservable
                .map(cursor -> {
                    List<DataUser> users = SqlUtils.convertToList(DataUser.class, cursor);
                    cursor.close();
                    return users;
                });
    }

    public static Observable.Transformer<Cursor, DataUser> toDataUser() {
        return cursorObservable -> cursorObservable
                .map(cursor -> {
                    DataUser user = SqlUtils.convertToModel(false, DataUser.class, cursor);
                    cursor.close();
                    return user;
                });
    }
}
