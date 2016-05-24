package com.worldventures.dreamtrips.core.rx.composer;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ImmediateComposer<T> implements Observable.Transformer<T, T> {

    private static final ImmediateComposer<Object> INSTANCE = new ImmediateComposer<>();

    @SuppressWarnings("unchecked")
    public static <T> ImmediateComposer<T> instance() {
        return (ImmediateComposer<T>) INSTANCE;
    }

    private ImmediateComposer() {
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate());
    }
}
