package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false)

public class LocaleModule {

    @Provides
    LocaleHelper provideLocaleHelper(LocalesHolder localesHolder) {
        return new LocaleHelper(localesHolder);
    }

    @Provides
    @Singleton
    LocaleSwitcher provideLocaleSwitcher(Context context) {
        return new LocaleSwitcher(context);
    }
}
