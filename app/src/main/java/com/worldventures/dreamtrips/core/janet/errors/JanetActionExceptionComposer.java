package com.worldventures.dreamtrips.core.janet.errors;

import io.techery.janet.ActionState;
import rx.Observable;

public final class JanetActionExceptionComposer<T> implements Observable.Transformer<ActionState<T>, ActionState<T>> {

    private static final JanetActionExceptionComposer INSTANCE = new JanetActionExceptionComposer();

    public static <T> JanetActionExceptionComposer<T> instance() {
        return (JanetActionExceptionComposer<T>) INSTANCE;
    }

    private JanetActionExceptionComposer() {
    }

    @Override
    public Observable<ActionState<T>> call(Observable<ActionState<T>> observable) {
        return observable.flatMap(state -> {
            if (state.status == ActionState.Status.FAIL) {
                return Observable.error(new JanetActionException(state.exception, state.action));
            }
            return Observable.just(state);
        });
    }
}
