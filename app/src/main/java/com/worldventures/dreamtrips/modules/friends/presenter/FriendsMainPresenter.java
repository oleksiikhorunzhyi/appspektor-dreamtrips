package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.events.RequestsLoadedEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

public class FriendsMainPresenter extends Presenter<FriendsMainPresenter.View> {

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    public void onEvent(RequestsLoadedEvent event) {
        view.setRecentItems(event.getCount());
    }

    public interface View extends Presenter.View {
        void setRecentItems(int count);
    }

    public void onEvent(UserClickedEvent event) {
        NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(event.getUser()))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(event.getUser().getId()));

    }

}
