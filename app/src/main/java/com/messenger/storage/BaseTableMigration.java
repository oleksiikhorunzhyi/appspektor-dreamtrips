package com.messenger.storage;

import android.database.Cursor;
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
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
        if (cursor == null) {
            Timber.e("Cursor is null");
            return;
        }
        if (!hasColumnName(cursor, columnName)) {
            try {
                sqLiteDatabase.execSQL("ALTER TABLE " + tableName + " ADD COLUMN `" + columnName + "` " + dataType);
            } catch (Exception e) {
                Timber.e("Error adding column " + columnName + ". Message: " + e.getMessage());
            }
        }
    }

    public void updateAllRowsInColumn(SQLiteDatabase sqLiteDatabase,
                                      String tableName, String column, String value) {
        sqLiteDatabase.execSQL("UPDATE " + tableName + " SET " + column + "='" + value + "'");
    }

    private static boolean hasColumnName(Cursor cursor, String columnName) {
        return cursor.getColumnIndex(columnName) != -1;
    }
}
