package com.worldventures.dreamtrips.core.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IoToMainComposer<T> implements Observable.Transformer<T, T> {

    public static <T> IoToMainComposer<T> get() {
        return new IoToMainComposer<>();
    }

    @Override
    public Observable<T> call(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
