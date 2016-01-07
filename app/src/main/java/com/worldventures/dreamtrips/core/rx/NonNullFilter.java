package com.worldventures.dreamtrips.core.rx;

import rx.Observable;

public class NonNullFilter<T> implements Observable.Transformer<T, T> {
    @Override
    public Observable<T> call(Observable<T> tObservable) {
        return tObservable.filter(obj -> obj != null);
    }
}
