package com.worldventures.dreamtrips.core.initializer;

import android.app.Application;

import com.newrelic.agent.android.NewRelic;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Inject;

public class NewrelicInitializer implements AppInitializer {

    static final String TOKEN = "AAb8d55ede8fd2f0aeaaf66b18b8b53af90e48b371";

    @Inject
    protected Application application;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        if (BuildConfig.NEWRELIC_ENABLED) {
            NewRelic.withApplicationToken(TOKEN).start(application);
        }
    }
}
