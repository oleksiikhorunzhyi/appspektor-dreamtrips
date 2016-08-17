package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Adapter;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataTranslation$Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;

@Migration(version = 4, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion4 extends BaseTableMigration {

   @Override
   public void migrate(SQLiteDatabase database) {
      rebuildTable(database, DataMessage.TABLE_NAME, new DataMessage$Adapter());
      rebuildTable(database, DataTranslation.TABLE_NAME, new DataTranslation$Adapter());
      rebuildTable(database, DataAttachment.TABLE_NAME, new DataAttachment$Adapter());
   }
}