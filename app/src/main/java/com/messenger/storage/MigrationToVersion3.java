package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

@Migration(version = 3, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion3 extends BaseMigration {

    @Override
    public void migrate(SQLiteDatabase database) {
        database.execSQL("DELETE FROM " + DataAttachment$Table.TABLE_NAME);
        database.execSQL("DELETE FROM " + DataMessage$Table.TABLE_NAME);
        database.execSQL("DELETE FROM " + DataConversation.TABLE_NAME);
        updateUserTable(database);
    }

    private void updateUserTable(SQLiteDatabase database) {
        database.execSQL("DROP TABLE " + DataUser.TABLE_NAME);

        DataUser$Adapter userAdapter = new DataUser$Adapter();
        database.execSQL(userAdapter.getCreationQuery());
    }
}
