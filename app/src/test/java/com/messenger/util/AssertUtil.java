package com.messenger.util;

import io.techery.janet.ActionState;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

public class AssertUtil {

    public static <T> void assertActionSuccess(TestSubscriber<ActionState<T>> subscriber, Func1<T, Boolean> assertPredicate) {
        assertAction(subscriber, assertPredicate, ActionState.Status.SUCCESS);
    }

    public static <T> void assertActionError(TestSubscriber<ActionState<T>> subscriber, Func1<T, Boolean> assertPredicate) {
        assertAction(subscriber, assertPredicate, ActionState.Status.FAIL);
    }

    public static <T> void assertAction(TestSubscriber<ActionState<T>> subscriber, Func1<T, Boolean> assertPredicate, ActionState.Status status) {
        assertActionStatusCount(subscriber, status, 1);
        assertPredicate.call(subscriber.getOnNextEvents().get(0).action);
    }

    public static <T> void assertActionStatusCount(TestSubscriber<ActionState<T>> subscriber, ActionState.Status status, int count) {
        subscriber.unsubscribe();
        subscriber.assertNoErrors();
        subscriber.assertUnsubscribed();
        assertStatusCount(subscriber, ActionState.Status.START, count);
        assertStatusCount(subscriber, status, count);
    }

    public static <T> void assertStatusCount(TestSubscriber<ActionState<T>> subscriber, ActionState.Status status, int count) {
        int i = 0;
        for (ActionState state : subscriber.getOnNextEvents()) {
            if (status == state.status) {
                i++;
            }
        }
        if (i != count) {
            throw new AssertionError("Number of events with status " + status + " differ; expected: " + count + ", actual: " + i);
        }
    }
}
