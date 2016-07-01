package com.messenger.storage.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.messenger.storage.MessengerDatabase;
import com.messenger.util.RxContentResolver;
import com.messenger.util.RxContentResolver.Query;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

class BaseDAO {
    private final Context context;
    private final ContentResolver contentResolver;
    private RxContentResolver rxContentResolver;

    BaseDAO(Context context, RxContentResolver rxContentResolver) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
        this.rxContentResolver = rxContentResolver;
    }

    protected Observable<Cursor> query(Query rxQuery, Uri... uris) {
        return rxContentResolver.query(rxQuery, uris);
    }

    protected ContentResolver getContentResolver() {
        return contentResolver;
    }

    protected Context getContext() {
        return context;
    }

    protected <T extends BaseModel> void bulkInsert(List<T> collection, ModelAdapter<T> adapter, Uri uri) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        for (T t : collection) {
            adapter.bindToContentValues(values, t);
            db.insertWithOnConflict(adapter.getTableName(), null, values,
                    ConflictAction.getSQLiteDatabaseAlgorithmInt(adapter.getInsertOnConflictAction()));
            values.clear();
        }
        contentResolver.notifyChange(uri, null);
    }

    protected SQLiteDatabase getWritableDatabase() {
        return FlowManager.getDatabase(MessengerDatabase.NAME).getWritableDatabase();
    }
}
