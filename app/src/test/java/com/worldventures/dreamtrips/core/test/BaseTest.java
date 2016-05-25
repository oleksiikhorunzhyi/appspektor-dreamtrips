package com.worldventures.dreamtrips.core.test;

import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;

import io.techery.janet.ActionService;
import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

public abstract class BaseTest {

    static {
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    protected static ActionService cachedService(ActionService service) {
        return new CacheResultWrapper(service);
    }

}
