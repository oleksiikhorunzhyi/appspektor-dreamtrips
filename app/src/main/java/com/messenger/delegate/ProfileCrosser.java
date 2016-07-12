package com.messenger.delegate;

import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;


public class ProfileCrosser {

    private final RouteCreator<Integer> routeCreator;
    private final Router router;

    @Inject
    public ProfileCrosser(Router router, @Named(PROFILE) RouteCreator<Integer> routeCreator) {
        this.router = router;
        this.routeCreator = routeCreator;
    }

    public void crossToProfile(DataUser user) {
        User socialUser = new User(user.getSocialId());
        router.moveTo(routeCreator.createRoute(socialUser.getId()), NavigationConfigBuilder.forActivity()
                .data(new UserBundle(socialUser))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .build());
    }
}
