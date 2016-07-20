package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Table;
import com.raizlabs.android.dbflow.annotation.Migration;

@Migration(version = 5, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion5 extends BaseTableMigration {

    @Override
    public void migrate(SQLiteDatabase database) {
        addColumn(database, DataConversation.TABLE_NAME, DATA_TYPE_INTEGER, DataConversation$Table.LEFTTIME);
    }
}
