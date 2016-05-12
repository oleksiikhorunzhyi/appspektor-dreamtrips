package com.worldventures.dreamtrips.core.janet;

import io.techery.janet.ActionState;
import rx.Observable;

public class ResultOnlyFilter<T> implements Observable.Transformer<ActionState<T>, ActionState<T>> {

    private static final ResultOnlyFilter INSTANCE = new ResultOnlyFilter();

    public static <T> ResultOnlyFilter<T> instance() {
        return (ResultOnlyFilter<T>) INSTANCE;
    }

    @Override
    public Observable<ActionState<T>> call(Observable<ActionState<T>> observable) {
        return observable.filter(actionState -> actionState.status == ActionState.Status.SUCCESS
                || actionState.status == ActionState.Status.FAIL);
    }
}
