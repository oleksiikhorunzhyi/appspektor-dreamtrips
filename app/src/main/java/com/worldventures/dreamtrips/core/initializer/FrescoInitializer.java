package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class FrescoInitializer implements AppInitializer {

    @Inject
    protected Context context;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        Fresco.initialize(context);
    }
}
