package com.worldventures.dreamtrips.core.rx.transfromer;

import java.util.List;

import rx.Observable;
import rx.functions.Func2;

import static rx.Observable.from;

public class ListSorter<T> implements Observable.Transformer<List<T>, List<T>> {

    private final Func2<T, T, Integer> predicate;

    public ListSorter(Func2<T, T, Integer> predicate) {
        this.predicate = predicate;
    }

    @Override public Observable<List<T>> call(Observable<List<T>> source) {
        return source.<List<T>>flatMap(items -> from(items).toSortedList(predicate));
    }
}