package com.worldventures.dreamtrips.core.rx.transfromer;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class DelayedComposer<T> implements Observable.Transformer<T, T> {

    protected long timeout;

    public DelayedComposer(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public Observable<T> call(Observable<T> observable) {
        return observable.throttleWithTimeout(timeout, TimeUnit.MILLISECONDS);
    }
}
