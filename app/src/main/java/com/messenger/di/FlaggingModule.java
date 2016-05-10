package com.messenger.di;

import com.techery.spares.session.SessionHolder;
import com.messenger.delegate.FlagsDelegate;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(
        injects = {},
        library = true, complete = false
)
public class FlaggingModule {

    @Provides
    @Singleton
    FlagsDelegate provideFlagsProvider(SessionHolder<UserSession> sessionHolder, Janet janet) {
        return new FlagsDelegate(sessionHolder, janet);
    }
}
