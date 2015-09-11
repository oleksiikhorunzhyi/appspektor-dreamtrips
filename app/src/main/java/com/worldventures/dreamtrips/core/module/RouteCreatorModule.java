package com.worldventures.dreamtrips.core.module;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.creator.ProfileRouteCreator;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                ProfileRouteCreator.class
        },
        complete = false,
        library = true
)
public class RouteCreatorModule {
    public static final String PROFILE = "profile";

    @Provides @Named(PROFILE)
    RouteCreator<Integer> provideProfileRouteCreator(SessionHolder<UserSession> appSessionHolder) {
        return new ProfileRouteCreator(appSessionHolder);
    }
}
