package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false)

public class LocaleModule {

    @Provides
    LocaleHelper provideLocaleHelper(SessionHolder<UserSession> appSessionHolder) {
        return new LocaleHelper(appSessionHolder);
    }

    @Provides
    @Singleton
    LocaleSwitcher provideLocaleSwitcher(Context context) {
        return new LocaleSwitcher(context);
    }
}
