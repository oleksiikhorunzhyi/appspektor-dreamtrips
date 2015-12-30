package com.messenger.storege.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.messenger.messengerservers.entities.User;
import com.messenger.util.RxContentResolver;
import com.messenger.util.RxContentResolver.Query;
import com.raizlabs.android.dbflow.config.FlowManager;

import rx.Observable;

class BaseDAO {
    private final Context context;
    private final ContentResolver contentResolver;
    private RxContentResolver rxContentResolver;

    BaseDAO(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();

        this.rxContentResolver = new RxContentResolver(contentResolver,
                query -> {
                    StringBuilder builder = new StringBuilder(query.selection);
                    if (!TextUtils.isEmpty(query.sortOrder)) {
                        builder.append(" ").append(query.sortOrder);
                    }
                    return FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                            .rawQuery(builder.toString(), query.selectionArgs);
                });
    }

    protected Observable<Cursor> query(Query rxQuery, Uri... uris) {
        return rxContentResolver.query(rxQuery, uris);
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    public Context getContext() {
        return context;
    }
}
