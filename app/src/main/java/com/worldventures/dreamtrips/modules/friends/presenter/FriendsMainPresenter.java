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

    public void onEvent(RequestsLoadedEvent event) {
        view.setRecentItems(event.getCount());
    }

    public interface View extends Presenter.View {
        void setRecentItems(int count);
    }

}
