package com.worldventures.dreamtrips.core.initializer;

import android.app.Application;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

public class JodaTimeInitializer implements AppInitializer {

    @Inject
    protected Application app;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        JodaTimeAndroid.init(app);
    }
}
