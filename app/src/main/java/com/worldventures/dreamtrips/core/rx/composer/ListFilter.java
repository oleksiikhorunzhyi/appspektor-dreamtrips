package com.worldventures.dreamtrips.core.rx.composer;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class ListFilter<T> implements Observable.Transformer<List<T>, List<T>> {

    private final Func1<T, Boolean>[] predicates;

    public ListFilter(@NonNull Func1<T, Boolean>... predicates) {
        this.predicates = predicates;
        if (Queryable.from(predicates).contains(predicate -> predicate == null))
            throw new IllegalArgumentException("Predicate cannot be null!");
    }

    @Override
    public Observable<List<T>> call(Observable<List<T>> source) {
        Observable<T> observable = source.flatMap(Observable::from);
        for (Func1<T, Boolean> predicate : predicates) {
            observable = observable.filter(predicate);
        }
        return observable.toList();
    }
}
