package com.messenger.storage;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Table;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Migration(version = 5, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion5 extends AlterTableMigration<DataConversation> {

    public MigrationToVersion5() {
        super(DataConversation.class);
        addColumn(long.class, DataConversation$Table.LEFTTIME);
    }
}
