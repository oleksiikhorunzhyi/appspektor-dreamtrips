package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.instabug.library.Instabug;
import com.instabug.wrapper.impl.v14.InstabugAnnotationActivity;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class InstabugInitializer implements AppInitializer {
    @Inject
    Context context;


    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        Instabug.initialize(context)
                .setAnnotationActivityClass(InstabugAnnotationActivity.class)
                .setShowIntroDialog(true)
                .setEnableOverflowMenuItem(true);
    }
}
