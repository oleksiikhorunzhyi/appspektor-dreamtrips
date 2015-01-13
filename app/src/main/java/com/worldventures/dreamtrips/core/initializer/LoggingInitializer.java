package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.utils.Logs;

public class LoggingInitializer implements AppInitializer {
    public static final String DEFAULT_TAG = "DreamTrips";

    @Override
    public void initialize(Injector injector) {
        Logs.init(DEFAULT_TAG, BuildConfig.DEBUG);
    }
}
