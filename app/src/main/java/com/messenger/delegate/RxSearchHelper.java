package com.messenger.delegate;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class RxSearchHelper<T> {

    public Observable<List<T>> search(Observable<List<T>> objectsObservable,
                                      Observable<CharSequence> searchFilter,
                                      @NonNull FilterPredicate<T> predicate) {
        return Observable
                .combineLatest(objectsObservable, searchFilter,
                        (list, sequence) -> applyFilter(list, sequence, predicate));
    }

    private List<T> applyFilter(List<T> items, CharSequence searchQuery, FilterPredicate<T> filterPredicate) {
        if (TextUtils.isEmpty(searchQuery)) return items;
        String query = searchQuery.toString();
        List<T> result = new ArrayList<>(items.size());
        for (T t : items) {
            if (filterPredicate.apply(t, query)) result.add(t);
        }
        return result;
    }

    public interface FilterPredicate<T> {
        boolean apply(T t, String searchFilter);
    }
}
