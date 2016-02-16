package com.worldventures.dreamtrips.core.initializer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.worldventures.dreamtrips.BuildConfig;

import net.hockeyapp.android.CrashManager;

import javax.inject.Inject;

public class HockeyInitializer implements AppInitializer {

    @Inject
    protected Application app;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        //
        app.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                CrashManager.register(activity, BuildConfig.HOCKEY_APP_ID);
            }
        });
    }
}
