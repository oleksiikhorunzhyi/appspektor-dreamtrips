package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.entities.DataAttachment$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Adapter;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataTranslation$Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

@Migration(version = 4, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion4 extends BaseMigration {

    @Override
    public void migrate(SQLiteDatabase database) {
        database.execSQL("DELETE FROM " + DataMessage$Table.TABLE_NAME);
        database.execSQL("DELETE FROM " + DataTranslation.TABLE_NAME);
        database.execSQL("DELETE FROM " + DataAttachment$Table.TABLE_NAME);

        dropTable(database, DataMessage.TABLE_NAME, new DataMessage$Adapter());
        dropTable(database, DataTranslation.TABLE_NAME, new DataTranslation$Adapter());
        dropTable(database, DataAttachment.TABLE_NAME, new DataAttachment$Adapter());
    }

    private void dropTable(SQLiteDatabase database, String tableName, ModelAdapter adapter){
        database.execSQL("DROP TABLE " + tableName);
        database.execSQL(adapter.getCreationQuery());
    }

}