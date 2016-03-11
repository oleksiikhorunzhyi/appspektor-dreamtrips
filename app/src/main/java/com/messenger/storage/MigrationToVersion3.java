package com.messenger.storage;

import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Migration(version = 3, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion3 extends AlterTableMigration<DataUser> {

    public MigrationToVersion3(Class<DataUser> table) {
        super(table);
        addColumn(boolean.class, DataUser$Table.HOST);
    }
}
