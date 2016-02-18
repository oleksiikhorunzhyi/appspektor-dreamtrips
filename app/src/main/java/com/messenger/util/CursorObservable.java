package com.messenger.util;

import android.database.Cursor;

import rx.Observable;
import rx.Subscriber;

public class CursorObservable implements Observable.OnSubscribe<Cursor> {
    private final Cursor originCursor;

    public static Observable<Cursor> create(Cursor cursor) {
        return Observable.create(new CursorObservable(cursor));
    }
    private CursorObservable(Cursor originCursor) {
        this.originCursor = originCursor;
    }

    @Override
    public void call(Subscriber<? super Cursor> subscriber) {
        try {
            for (int i = 0; i < originCursor.getCount(); i++) {
                originCursor.moveToPosition(i);
                subscriber.onNext(originCursor);
            }
            subscriber.onCompleted();
        } catch (Throwable throwable) {
            subscriber.onError(throwable);
        } finally {
            originCursor.close();
        }
    }
}
