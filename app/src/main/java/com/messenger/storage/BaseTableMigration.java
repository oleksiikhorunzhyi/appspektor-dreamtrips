package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import timber.log.Timber;

public abstract class BaseTableMigration extends BaseMigration {

    public static final String DATA_TYPE_TEXT = "TEXT";
    public static final String DATA_TYPE_INTEGER = "INTEGER";

    protected void rebuildTable(SQLiteDatabase database, String tableName, ModelAdapter adapter) {
        database.execSQL("DROP TABLE " + tableName);
        database.execSQL(adapter.getCreationQuery());
    }

    public void addColumn(SQLiteDatabase sqLiteDatabase, String tableName,
                          String dataType, String columnName) {
        StringBuilder query = new StringBuilder();
        query.append("ALTER TABLE ");
        query.append(tableName).append(" ADD ");
        query.append(columnName).append(" ").append(dataType);
        sqLiteDatabase.execSQL(query.toString());
    }

    public void updateAllRowsInColumn(SQLiteDatabase sqLiteDatabase,
                                      String tableName, String column, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(tableName).append(" ");
        sb.append("SET ").append(column).append("='").append(value).append("'");
        sqLiteDatabase.execSQL(sb.toString());
    }
}
