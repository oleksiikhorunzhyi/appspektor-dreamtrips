package com.worldventures.dreamtrips;

import org.junit.Assert;

import io.techery.janet.ActionState;
import io.techery.janet.CancelException;
import io.techery.janet.JanetException;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

public final class AssertUtil {

    private AssertUtil() {
    }

    public static <T> void assertActionSuccess(TestSubscriber<ActionState<T>> subscriber, Func1<T, Boolean> assertPredicate) {
        subscriber.unsubscribe();
        subscriber.assertNoErrors();
        subscriber.assertUnsubscribed();
        assertStatusCount(subscriber, ActionState.Status.START, 1);
        assertStatusCount(subscriber, ActionState.Status.SUCCESS, 1);
        Assert.assertTrue(assertPredicate.call(subscriber.getOnNextEvents()
                .get(subscriber.getOnNextEvents().size() - 1).action));
    }

    public static <T> void assertSubscriberWithSingleValue(TestSubscriber<T> subscriber) {
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertUnsubscribed();
    }

    public static <T> void assertSubscriberWithoutErrorAndValues(TestSubscriber<T> subscriber) {
        subscriber.assertNoErrors();
        subscriber.assertNoValues();
        subscriber.assertUnsubscribed();
    }

    public static <T> void assertActionCanceled(TestSubscriber<ActionState<T>> subscriber) {
        subscriber.unsubscribe();
        subscriber.assertNoErrors();
        subscriber.assertUnsubscribed();
        assertStatusCount(subscriber, ActionState.Status.START, 1);
        assertStatusCount(subscriber, ActionState.Status.FAIL, 1);
        Assert.assertThat(subscriber.getOnNextEvents().get(1).exception, instanceOf(CancelException.class));
    }

    public static <T> void assertActionFail(TestSubscriber<ActionState<T>> subscriber, Func1<JanetException, Boolean> assertPredicate) {
        subscriber.unsubscribe();
        subscriber.assertNoErrors();
        subscriber.assertUnsubscribed();
        assertStatusCount(subscriber, ActionState.Status.START, 1);
        assertStatusCount(subscriber, ActionState.Status.FAIL, 1);
        Assert.assertTrue(assertPredicate.call(subscriber.getOnNextEvents()
                .get(subscriber.getOnNextEvents().size() - 1).exception));
    }

    public static <T> void assertNoStatuses(TestSubscriber<ActionState<T>> subscriber) {
        subscriber.assertNoErrors();
        subscriber.assertValueCount(0);
        subscriber.assertUnsubscribed();
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
