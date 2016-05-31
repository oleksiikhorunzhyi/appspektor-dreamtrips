package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

public abstract class BaseDropTableMigration extends BaseMigration {

    protected void rebuildTable(SQLiteDatabase database, String tableName, ModelAdapter adapter){
        database.execSQL("DROP TABLE " + tableName);
        database.execSQL(adapter.getCreationQuery());
    }
}
