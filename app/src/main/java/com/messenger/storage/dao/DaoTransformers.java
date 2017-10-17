package com.messenger.storage.dao;

import android.database.Cursor;

import com.messenger.entities.DataUser$Table;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public final class DaoTransformers {

   private DaoTransformers() {
   }

   public static <T extends Model> Observable.Transformer<Cursor, T> toEntity(Class<T> table) {
      return cursorObservable -> cursorObservable.map(cursor -> {
         T models = SqlUtils.convertToModel(false, table, cursor);
         cursor.close();
         return models;
      });
   }

   public static <T extends Model> Observable.Transformer<Cursor, List<T>> toEntityList(Class<T> table) {
      return cursorObservable -> cursorObservable.map(cursor -> {
         List<T> models = SqlUtils.convertToList(table, cursor);
         cursor.close();
         return models;
      });
   }

   public static Observable.Transformer<Cursor, List<String>> toUserId() {
      return cursorObservable -> cursorObservable.map(cursor -> {
         ArrayList<String> userIds = new ArrayList<>(cursor.getCount());
         while (cursor.moveToNext()) {
            userIds.add(cursor.getString(cursor.getColumnIndex(DataUser$Table._ID)));
         }
         cursor.close();
         return userIds;
      });
   }

}
