package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.rx.debug.TimberDebugHook;

import rx.plugins.RxJavaPlugins;

public class RxJavaLoggingInitializer implements AppInitializer {

    @Override
    public void initialize(Injector injector) {
//        RxJavaPlugins.getInstance().registerObservableExecutionHook(new TimberDebugHook());
    }
}
