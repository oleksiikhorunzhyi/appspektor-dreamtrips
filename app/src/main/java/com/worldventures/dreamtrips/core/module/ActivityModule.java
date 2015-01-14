package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.DreamTripsApplication;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.adapter.item.PhotoItem;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.view.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoActivityPM;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoFragmentPM;
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
                CreatePhotoActivityPM.class,
                CreatePhotoFragmentPM.class,
                ProfileFragment.class,
                PhotoItem.class
        },
        complete = false,
        library = true
)
public class ActivityModule {

    BaseActivity baseActivity;

    public ActivityModule(BaseActivity baseActivity) {
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

}
