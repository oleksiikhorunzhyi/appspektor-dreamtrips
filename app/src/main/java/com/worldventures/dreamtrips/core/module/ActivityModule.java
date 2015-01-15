package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.adapter.item.PhotoItem;
import com.worldventures.dreamtrips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.view.fragment.LoginFragment;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.view.fragment.WebViewFragment;
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

@Module(
        injects = {
                LaunchActivity.class,
                MainActivity.class,
                LoginActivity.class,

                LaunchActivityPresentation.class,
                LoginActivityPresentation.class,
                LoginFragmentPresentation.class,
                WebViewFragmentPresentation.class,
                BaseActivityPresentation.class,
                ProfileFragmentPresentation.class,
                MainActivityPresentation.class,
                NavigationDrawerAdapter.class,
                TripImagesTabsFragmentPresentation.class,
                TripImagesListFragmentPresentation.class,
                CreatePhotoActivityPM.class,
                CreatePhotoFragmentPM.class,

                NavigationDrawerFragment.class,
                ProfileFragment.class,
                DreamTripsFragment.class,
                TripImagesTabsFragment.class,
                LoginFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                WebViewFragment.class,

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
