package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

public abstract class BaseTableMigration extends BaseMigration {

    public static final String DATA_TYPE_TEXT = "TEXT";
    public static final String DATA_TYPE_INTEGER = "INTEGER";

    protected void rebuildTable(SQLiteDatabase database, String tableName, ModelAdapter adapter) {
        database.execSQL("DROP TABLE " + tableName);
        database.execSQL(adapter.getCreationQuery());
    }

    public void addColumn(SQLiteDatabase sqLiteDatabase, String tableName,
                          String dataType, String columnName) {
        sqLiteDatabase.execSQL("ALTER TABLE " + tableName + " ADD " + columnName + " " + dataType);
    }

    public void updateAllRowsInColumn(SQLiteDatabase sqLiteDatabase,
                                      String tableName, String column, String value) {
        sqLiteDatabase.execSQL("UPDATE " + tableName + " SET " + column + "='" + value + "'");
    }
}
