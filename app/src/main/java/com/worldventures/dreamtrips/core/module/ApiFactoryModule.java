package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.factory.GoroRxApiFactory;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ApiFactoryModule {

    @Provides
    @Singleton
    RxApiFactory provideApiFactory(@ForApplication Context context, @ForApplication Injector injector) {
        return new GoroRxApiFactory(context, injector);
    }
}
