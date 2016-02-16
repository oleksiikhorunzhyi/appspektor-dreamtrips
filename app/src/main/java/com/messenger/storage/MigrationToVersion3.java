package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

@Migration(version = 3, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion3 extends BaseMigration {
    @Override
    public void migrate(SQLiteDatabase database) {
        String dropTable = "DROP TABLE ";
        database.execSQL(dropTable + DataConversation.TABLE_NAME);

        DataConversation$Adapter dataConversationAdapter = new DataConversation$Adapter();

        database.execSQL(dataConversationAdapter.getCreationQuery());
    }
}
