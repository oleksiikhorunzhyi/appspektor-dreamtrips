package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.factory.GoroRxApiFactory;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.store.RetryLoginComposer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ApiFactoryModule {

    @Provides
    @Singleton
    RxApiFactory provideApiFactory(@ForApplication Context context, RetryLoginComposer retryLoginComposer) {
        return new GoroRxApiFactory(context, retryLoginComposer);
    }

    @Provides
    @Singleton
    RetryLoginComposer provideRetryLoginComposer(DreamTripsApi dreamTripsApi,
                                                 SessionHolder<UserSession> appSessionHolder) {
        return new RetryLoginComposer(dreamTripsApi, appSessionHolder);
    }
}
