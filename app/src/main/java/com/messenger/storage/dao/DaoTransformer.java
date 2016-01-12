package com.messenger.storage.dao;

import android.database.Cursor;

import rx.Observable;

public class DaoTransformer implements Observable.Transformer<Cursor, Cursor> {
    @Override
    public Observable<Cursor> call(Observable<Cursor> cursorObservable) {
        Cursor[] cursors = {null};
        return cursorObservable
                .doOnNext(cursor -> cursors[0] = cursor)
                .doOnUnsubscribe(() -> {
                    Cursor cursor = cursors[0];
                    if (cursor != null && !cursor.isClosed()) cursor.close();
                });
    }
}
