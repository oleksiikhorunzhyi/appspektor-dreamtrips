package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.instabug.library.Instabug;
import com.instabug.wrapper.impl.v14.InstabugAnnotationActivity;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Inject;

public class InstabugInitializer implements AppInitializer {
    public static final String PROD = "prod";

    @Inject
    protected Context context;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        if (!PROD.equals(BuildConfig.FLAVOR)) {
            Instabug.initialize(context)
                    .setCrashReportingEnabled(false)
                    .setAnnotationActivityClass(InstabugAnnotationActivity.class)
                    .setShowIntroDialog(true)
                    .setEnableOverflowMenuItem(true);
        }
    }
}
