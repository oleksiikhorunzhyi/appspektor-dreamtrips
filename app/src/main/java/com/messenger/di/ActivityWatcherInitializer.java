package com.messenger.di;

import android.content.Context;

import com.messenger.synchmechanism.ActivityWatcher;
import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;

import javax.inject.Inject;

public class ActivityWatcherInitializer implements AppInitializer {

    @Inject
    @ForApplication
    Context context;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        new ActivityWatcher(context, MessengerConnector.getInstance());
    }
}
