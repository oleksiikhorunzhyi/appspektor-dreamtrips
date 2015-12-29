package com.messenger.util;

import android.content.Context;

import com.messenger.messengerservers.entities.Conversation;
import com.raizlabs.android.dbflow.config.FlowManager;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UnreadConversationObservable {

    private static final int TIMEOUT = 1000;

    RxContentResolver contentResolver;
    Observable<Integer> observable;

    public UnreadConversationObservable(Context context) {
        contentResolver = new RxContentResolver(context.getContentResolver(),
                query -> FlowManager.getDatabaseForTable(Conversation.class).getWritableDatabase()
                        .rawQuery(query.selection, query.selectionArgs));

        String query = "SELECT COUNT(*) AS UNREAD_CONV FROM " + Conversation.TABLE_NAME
                + " WHERE " + Conversation.COLUMN_UNREAD_COUNT + " > 0 ";

        RxContentResolver.Query.Builder queryBuilder = new RxContentResolver.Query.Builder(Conversation.CONTENT_URI)
                .withSelection(query);

        observable = contentResolver
                .query(queryBuilder.build())
                .onBackpressureLatest()
                .map(cursor -> cursor.moveToFirst() ? cursor.getInt(0) : 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Subscription subscribe(Action1<Integer> action) {
        return observable.subscribe(action, throwable -> {});
    }

}
