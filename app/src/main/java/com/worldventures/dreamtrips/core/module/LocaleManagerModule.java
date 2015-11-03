package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.worldventures.dreamtrips.core.utils.LocaleManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false)

public class LocaleManagerModule {

    @Provides
    @Singleton
    LocaleManager provideLocaleManager(Context context) {
        return new LocaleManager(context);
    }
}
