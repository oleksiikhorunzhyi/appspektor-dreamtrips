package com.messenger.util;

import rx.observers.TestSubscriber;

public class TestSubscriberAssertUtils {

    public static void isNoErrorAndUnsubscribed(TestSubscriber testSubscriber) {
        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();
        testSubscriber.assertUnsubscribed();
    }

}
