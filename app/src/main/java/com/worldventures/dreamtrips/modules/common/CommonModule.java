package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.ComponentsConfig;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.SimpleStreamPlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.modules.feed.FeedModule;
import com.worldventures.dreamtrips.modules.infopages.InfoModule;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.profile.ProfileModule;
import com.worldventures.dreamtrips.modules.reptools.ReptoolsModule;
import com.worldventures.dreamtrips.modules.trips.TripsModule;
import com.worldventures.dreamtrips.modules.tripsimages.TripsImagesModule;
import com.worldventures.dreamtrips.modules.video.VideoModule;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;

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
                ShareFragment.class,
                SimpleStreamPlayerActivity.class,
                FilterableArrayListAdapter.class,
                DraggableArrayListAdapter.class,
                NavigationDrawerFragment.class,
                DownloadVideoListener.class,
                PresentationVideosPresenter.class,
                ComponentActivity.class,
                ComponentPresenter.class,
                CopyFileCommand.class

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
    ComponentsConfig provideComponentsConfig(FeatureManager featureManager) {
        List<String> activeComponents = new ArrayList<>();

        featureManager.with(Feature.SOCIAL, () -> activeComponents.add(FeedModule.FEED));

        featureManager.with(Feature.TRIPS, () -> activeComponents.add(TripsModule.TRIPS));

        activeComponents.add(TripsModule.OTA);
        activeComponents.add(TripsImagesModule.TRIP_IMAGES);
        activeComponents.add(VideoModule.MEMBERSHIP);
        activeComponents.add(BucketListModule.BUCKETLIST);
        activeComponents.add(ProfileModule.MY_PROFILE);

        featureManager.with(Feature.REP_TOOLS, () -> activeComponents.add(ReptoolsModule.REP_TOOLS));

        activeComponents.add(InfoModule.FAQ);
        activeComponents.add(InfoModule.TERMS);

        activeComponents.add(TripsModule.MAP_TRIPS);

        return new ComponentsConfig(activeComponents);
    }

}
