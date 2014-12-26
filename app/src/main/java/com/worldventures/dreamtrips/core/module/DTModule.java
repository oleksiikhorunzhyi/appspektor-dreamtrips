package com.worldventures.dreamtrips.core.module;


import android.content.Context;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {DTApplication.class, LaunchActivity.class, MainActivity.class, LoginActivity.class},
        includes = {ApiModule.class},
        library = true,
        complete = false)
public class DTModule {
    DTApplication app;

    public DTModule(DTApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    DTApplication provideApplicationContext() {
        return app;
    }

    @Provides
    Context provideContext() {
        return app;
    }

}
