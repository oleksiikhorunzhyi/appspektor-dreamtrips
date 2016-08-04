package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.QueryTripsFilterDataInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.AuthorizedDataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(injects = {
        AuthorizedDataManager.class
}, library = true, complete = false)
public class SessionProcessingModule {

    @Provides
    @Singleton
    public QueryTripsFilterDataInteractor provideQueryTripsFilterDataInteractor(Janet janet) {
        return new QueryTripsFilterDataInteractor(janet);
    }

    @Singleton
    @Provides
    public AuthInteractor provideAuthInteractor(Janet janet) {
        return new AuthInteractor(janet);
    }
}
