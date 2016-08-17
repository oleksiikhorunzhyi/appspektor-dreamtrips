package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage$Table;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

@Migration(version = 2, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion2 extends BaseMigration {

   @Override
   public void migrate(SQLiteDatabase database) {
      database.execSQL("DELETE FROM " + DataAttachment$Table.TABLE_NAME);
      database.execSQL("DELETE FROM " + DataMessage$Table.TABLE_NAME);
   }
}
