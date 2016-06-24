package com.messenger.storage;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Migration(version = 6, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion6 extends AlterTableMigration<DataMessage> {

    public MigrationToVersion6() {
        super(DataMessage.class);
        addColumn(String.class, DataMessage$Table.TYPE);
    }
}
