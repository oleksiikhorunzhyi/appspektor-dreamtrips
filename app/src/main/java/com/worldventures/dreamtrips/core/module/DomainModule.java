package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.adapter.item.PhotoItem;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookAlbumItem;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookPhotoItem;
import com.worldventures.dreamtrips.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.view.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoActivityPM;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoFragmentPM;
import com.worldventures.dreamtrips.view.presentation.FullScreenActivityPM;
import com.worldventures.dreamtrips.view.presentation.FullScreenPhotoFragmentPM;
import com.worldventures.dreamtrips.view.presentation.LaunchActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.LoginActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.LoginFragmentPresentation;
import com.worldventures.dreamtrips.view.presentation.MainActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.ProfileFragmentPresentation;
import com.worldventures.dreamtrips.view.presentation.TripImagesListFragmentPresentation;
import com.worldventures.dreamtrips.view.presentation.TripImagesTabsFragmentPresentation;
import com.worldventures.dreamtrips.view.presentation.WebViewFragmentPresentation;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        addsTo = DTModule.class,
        injects = {
                LaunchActivityPresentation.class,
                LoginActivityPresentation.class,
                LoginFragmentPresentation.class,
                WebViewFragmentPresentation.class,
                BaseActivityPresentation.class,
                ProfileFragmentPresentation.class,
                MainActivityPresentation.class,
                NavigationDrawerFragment.class,
                NavigationDrawerAdapter.class,
                TripImagesTabsFragmentPresentation.class,
                TripImagesListFragmentPresentation.class,
                FullScreenActivityPM.class,
                FullScreenPhotoFragmentPM.class,
                FullScreenPhotoFragment.class,
                CreatePhotoActivityPM.class,
                CreatePhotoFragmentPM.class,
                ProfileFragment.class,
                CreatePhotoFragment.class,
                FacebookAlbumItem.class,
                FacebookPhotoItem.class,
                PhotoItem.class
        },
        library = true
)
public class DomainModule {

    BaseActivity baseActivity;

    public DomainModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Provides
    public ActivityRouter provideActivityCompass() {
        return new ActivityRouter(baseActivity);
    }

    @Provides
    @Singleton
    public UniversalImageLoader provideImageLoader() {
        return new UniversalImageLoader();
    }

    @Provides
    public FragmentCompass provideFragmentCompass() {
        return new FragmentCompass(baseActivity);
    }

    @Provides
    @Singleton
    public DataManager provideDataManager(DTApplication app) {
        return new DataManager(app);
    }

    @Provides
    @Singleton
    public SessionManager provideSessionManager(DTApplication app) {
        return new SessionManager(app);
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

}
