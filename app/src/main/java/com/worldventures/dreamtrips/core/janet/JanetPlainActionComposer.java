package com.worldventures.dreamtrips.core.janet;

import com.worldventures.dreamtrips.core.janet.errors.JanetActionException;

import io.techery.janet.ActionState;
import rx.Observable;

public class JanetPlainActionComposer<A> implements Observable.Transformer<ActionState<A>, A> {

    private static final JanetPlainActionComposer INSTANCE = new JanetPlainActionComposer();

    public static <T> JanetPlainActionComposer<T> instance() {
        return (JanetPlainActionComposer<T>) INSTANCE;
    }

    private JanetPlainActionComposer() {
    }

    @Override
    public Observable<A> call(final Observable<ActionState<A>> observable) {
        return observable.flatMap(state -> {
            switch (state.status) {
                case START:
                    return Observable.empty();
                case PROGRESS:
                    return Observable.empty();
                case SUCCESS:
                    return Observable.just(state.action);
                case FAIL:
                    return Observable.error(new JanetActionException(state.exception, state.action));
                default:
                    throw new IllegalArgumentException("Action status is unknown");
            }
        });
    }
}
