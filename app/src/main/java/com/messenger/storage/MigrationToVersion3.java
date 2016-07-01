package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataConversation$Adapter;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser$Adapter;
import com.messenger.entities.DataUser$Table;
import com.raizlabs.android.dbflow.annotation.Migration;

@Migration(version = 3, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion3 extends BaseTableMigration {

    @Override
    public void migrate(SQLiteDatabase database) {
        database.execSQL("DELETE FROM " + DataAttachment$Table.TABLE_NAME);
        database.execSQL("DELETE FROM " + DataMessage$Table.TABLE_NAME);

        rebuildTable(database, DataConversation$Table.TABLE_NAME, new DataConversation$Adapter());
        rebuildTable(database, DataUser$Table.TABLE_NAME, new DataUser$Adapter());
    }
}
