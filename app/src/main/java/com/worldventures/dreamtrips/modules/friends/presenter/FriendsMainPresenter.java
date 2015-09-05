package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.events.RequestsLoadedEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

public class FriendsMainPresenter extends Presenter<FriendsMainPresenter.View> {

    public void onEvent(RequestsLoadedEvent event) {
        view.setRecentItems(event.getCount());
    }

    public interface View extends Presenter.View {
        void setRecentItems(int count);
    }

    public void onEvent(UserClickedEvent event) {//TODO
        NavigationBuilder.create().with(activityRouter).data(new UserBundle(event.getUser()));

    }

}
