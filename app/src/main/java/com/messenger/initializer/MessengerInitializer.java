package com.messenger.initializer;

import android.app.Activity;
import android.app.Application;

import com.messenger.notification.UnhandledMessageWatcher;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;

import javax.inject.Inject;

public class MessengerInitializer implements AppInitializer {
    @Inject Application app;
    @Inject UnhandledMessageWatcher unhandledMessageWatcher;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        //// TODO: 12/29/15 refactor
        app.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

            @Override
            public void onActivityStarted(Activity activity) {
                unhandledMessageWatcher.start(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                unhandledMessageWatcher.stop(activity);
            }
        });
    }
}
