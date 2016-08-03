package com.worldventures.dreamtrips.core.module;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.store.RetryLoginComposer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ApiFactoryModule {

    @Provides
    @Singleton
    RetryLoginComposer provideRetryLoginComposer(DreamTripsApi dreamTripsApi,
                                                 SessionHolder<UserSession> appSessionHolder) {
        return new RetryLoginComposer(dreamTripsApi, appSessionHolder);
    }
}
