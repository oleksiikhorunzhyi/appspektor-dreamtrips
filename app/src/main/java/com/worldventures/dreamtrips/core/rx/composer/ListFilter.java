package com.worldventures.dreamtrips.core.rx.composer;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static rx.Observable.from;

public class ListFilter<T> implements Observable.Transformer<List<T>, List<T>> {

    private final Func1<T, Boolean> predicate;

    public ListFilter(Func1<T, Boolean> predicate) {
        this.predicate = predicate;
    }

    @Override public Observable<List<T>> call(Observable<List<T>> source) {
        return source.<List<T>>flatMap(items -> from(items).filter(predicate).toList());
    }
}
