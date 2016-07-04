package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage$Table;
import com.messenger.messengerservers.constant.MessageType;
import com.raizlabs.android.dbflow.annotation.Migration;

@Migration(version = 6, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion6 extends BaseTableMigration {

    @Override
    public void migrate(SQLiteDatabase database) {
        addColumn(database, DataConversation$Table.TABLE_NAME, DATA_TYPE_INTEGER, DataConversation$Table.CLEARTIME);
        addColumn(database, DataMessage$Table.TABLE_NAME, DATA_TYPE_TEXT, DataMessage$Table.TYPE);
        updateAllRowsInColumn(database, DataMessage$Table.TABLE_NAME, DataMessage$Table.TYPE, MessageType.MESSAGE);
    }
}