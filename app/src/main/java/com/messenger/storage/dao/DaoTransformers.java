package com.messenger.storage.dao;

import android.database.Cursor;

import com.messenger.entities.DataUser$Table;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class DaoTransformers {

   public static <ModelClass extends Model> Observable.Transformer<Cursor, ModelClass> toEntity(Class<ModelClass> table) {
      return cursorObservable -> cursorObservable.map(cursor -> {
         ModelClass models = SqlUtils.convertToModel(false, table, cursor);
         cursor.close();
         return models;
      });
   }

   public static <ModelClass extends Model> Observable.Transformer<Cursor, List<ModelClass>> toEntityList(Class<ModelClass> table) {
      return cursorObservable -> cursorObservable.map(cursor -> {
         List<ModelClass> models = SqlUtils.convertToList(table, cursor);
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
