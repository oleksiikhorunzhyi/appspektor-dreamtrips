package com.worldventures.dreamtrips.modules.common;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.ComponentsConfig;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.SimpleStreamPlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.adapter.MyDraggableSwipeableItemAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                ActivityPresenter.class,
                LaunchActivityPresenter.class,
                MainActivityPresenter.class,
                NavigationDrawerPresenter.class,
                Presenter.class,
                SharePresenter.class,

                LaunchActivity.class,
                MainActivity.class,
                PlayerActivity.class,
                ShareActivity.class,
                SimpleStreamPlayerActivity.class,
                FilterableArrayListAdapter.class,
                MyDraggableSwipeableItemAdapter.class,
                NavigationDrawerFragment.class,
                NavigationDrawerFragment.class,
                NavigationDrawerAdapter.class,
        },
        complete = false,
        library = true
)
public class CommonModule {

    @Provides
    RootComponentsProvider provideRootComponentsProvider(Set<ComponentDescription> descriptions, ComponentsConfig config) {
        return new RootComponentsProvider(descriptions, config);
    }

    @Provides
    ComponentsConfig provideComponentsConfig(SessionHolder<UserSession> appSession) {
        List<String> activeComponents = new ArrayList<>();

        User user = appSession.get().get().getUser();

        if (user.isMember()) {
            activeComponents.add(TripsModule.TRIPS);
        }

        activeComponents.add(TripsModule.OTA);
        activeComponents.add(TripsImagesModule.TRIP_IMAGES);
        activeComponents.add(InfoModule.MEMBERSHIP);
        activeComponents.add(BucketListModule.BUCKETLIST);
        activeComponents.add(ProfileModule.MY_PROFILE);

        if (user.isRep()) {
            activeComponents.add(ReptoolsModule.REP_TOOLS);
        }

        activeComponents.add(InfoModule.FAQ);
        activeComponents.add(InfoModule.TERMS_OF_SERVICE);

        return new ComponentsConfig(activeComponents);
    }

}
