package com.messenger.storage.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.messenger.entities.DataUser;
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

    @Deprecated
    BaseDAO(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();

        this.rxContentResolver = new RxContentResolver(contentResolver,
                query -> {
                    StringBuilder builder = new StringBuilder(query.selection);
                    if (!TextUtils.isEmpty(query.sortOrder)) {
                        builder.append(" ").append(query.sortOrder);
                    }
                    return FlowManager.getDatabaseForTable(DataUser.class).getWritableDatabase()
                            .rawQuery(builder.toString(), query.selectionArgs);
                });
    }

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
        for (T t : collection) {
            adapter.bindToContentValues(values, t);
            FlowManager.getDatabase(MessengerDatabase.NAME).getWritableDatabase()
                    .insertWithOnConflict(adapter.getTableName(), null, values, ConflictAction.getSQLiteDatabaseAlgorithmInt(adapter.getInsertOnConflictAction()));
            values.clear();
        }
        contentResolver.notifyChange(uri, null);
    }
}