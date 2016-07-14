package com.worldventures.dreamtrips.messenger.util;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

public class BaseTest {

    static {
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
