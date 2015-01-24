package com.worldventures.dreamtrips.core.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.uploader.job.UploadJob;
import com.worldventures.dreamtrips.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.presentation.BookItActivityPresentation;
import com.worldventures.dreamtrips.presentation.BucketListFragmentPM;
import com.worldventures.dreamtrips.presentation.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.presentation.CreatePhotoActivityPM;
import com.worldventures.dreamtrips.presentation.CreatePhotoFragmentPM;
import com.worldventures.dreamtrips.presentation.DetailTripActivityPM;
import com.worldventures.dreamtrips.presentation.DetailedImagePagerFragmentPresentation;
import com.worldventures.dreamtrips.presentation.DetailedTripFragmentPM;
import com.worldventures.dreamtrips.presentation.DreamTripsFragmentPM;
import com.worldventures.dreamtrips.presentation.DummyPresentationModel;
import com.worldventures.dreamtrips.presentation.FacebookAlbumFragmentPM;
import com.worldventures.dreamtrips.presentation.FacebookPhotoFragmentPM;
import com.worldventures.dreamtrips.presentation.FacebookPickPhotoActivityPM;
import com.worldventures.dreamtrips.presentation.FiltersFragmentPM;
import com.worldventures.dreamtrips.presentation.FullScreenActivityPM;
import com.worldventures.dreamtrips.presentation.FullScreenPhotoFragmentPM;
import com.worldventures.dreamtrips.presentation.LaunchActivityPresentation;
import com.worldventures.dreamtrips.presentation.LoginActivityPresentation;
import com.worldventures.dreamtrips.presentation.LoginFragmentPresentation;
import com.worldventures.dreamtrips.presentation.MainActivityPresentation;
import com.worldventures.dreamtrips.presentation.MembershipPM;
import com.worldventures.dreamtrips.presentation.NavigationDrawerPM;
import com.worldventures.dreamtrips.presentation.ProfileFragmentPresentation;
import com.worldventures.dreamtrips.presentation.TripImagesListFragmentPresentation;
import com.worldventures.dreamtrips.presentation.TripImagesTabsFragmentPresentation;
import com.worldventures.dreamtrips.presentation.WebViewFragmentPresentation;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenTripImageActivity;
import com.worldventures.dreamtrips.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.adapter.item.PhotoItem;
import com.worldventures.dreamtrips.view.cell.ActivityCell;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.cell.FiltersCell;
import com.worldventures.dreamtrips.view.cell.PhotoCell;
import com.worldventures.dreamtrips.view.cell.PhotoUploadCell;
import com.worldventures.dreamtrips.view.cell.RegionCell;
import com.worldventures.dreamtrips.view.cell.SoldOutCell;
import com.worldventures.dreamtrips.view.cell.ThemeHeaderCell;
import com.worldventures.dreamtrips.view.cell.TripCell;
import com.worldventures.dreamtrips.view.cell.VideoCell;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookAlbumItem;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookPhotoItem;
import com.worldventures.dreamtrips.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.view.fragment.DetailedImagePagerFragment;
import com.worldventures.dreamtrips.view.fragment.DetailedTripFragment;
import com.worldventures.dreamtrips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.LoginFragment;
import com.worldventures.dreamtrips.view.fragment.MemberShipFragment;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                LaunchActivity.class,
                BookItActivity.class,
                BookItActivityPresentation.class,
                MainActivity.class,
                LoginActivity.class,
                FullScreenPhotoActivity.class,
                FullScreenTripImageActivity.class,
                DetailTripActivity.class,
                FBPickPhotoActivity.class,
                CreatePhotoActivity.class,
                LaunchActivityPresentation.class,
                LoginActivityPresentation.class,
                LoginFragmentPresentation.class,
                WebViewFragmentPresentation.class,
                BaseActivityPresentation.class,
                ProfileFragmentPresentation.class,
                MainActivityPresentation.class,
                FacebookPickPhotoActivityPM.class,
                NavigationDrawerAdapter.class,
                TripImagesTabsFragmentPresentation.class,
                TripImagesListFragmentPresentation.class,
                DreamTripsFragmentPM.class,
                DetailedTripFragmentPM.class,
                DetailTripActivityPM.class,
                FullScreenActivityPM.class,
                FullScreenPhotoFragmentPM.class,
                FullScreenPhotoFragment.class,
                CreatePhotoActivityPM.class,
                CreatePhotoFragmentPM.class,
                DummyPresentationModel.class,
                FacebookAlbumFragmentPM.class,
                FacebookPhotoFragmentPM.class,
                FiltersFragmentPM.class,
                DetailedImagePagerFragmentPresentation.class,

                NavigationDrawerFragment.class,
                FiltersFragment.class,
                MembershipPM.class,
                BucketTabsFragmentPM.class,
                BucketListFragmentPM.class,

                NavigationDrawerFragment.class,
                MemberShipFragment.class,
                ProfileFragment.class,
                DreamTripsFragment.class,
                DetailedTripFragment.class,
                TripImagesTabsFragment.class,
                LoginFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                StaticInfoFragment.class,
                FacebookAlbumFragment.class,
                FacebookPhotoFragment.class,
                StaticInfoFragment.FAQFragment.class,
                StaticInfoFragment.TermsAndConditionsFragment.class,
                StaticInfoFragment.BookItFragment.class,
                BucketTabsFragment.class,
                BucketListFragment.class,
                DetailedImagePagerFragment.class,

                CreatePhotoFragment.class,
                FacebookAlbumItem.class,
                FacebookPhotoItem.class,
                NavigationDrawerPM.class,
                RegionCell.class,
                TripCell.class,
                PhotoItem.class,
                PhotoCell.class,
                PhotoUploadCell.class,
                FiltersCell.class,
                VideoCell.class,
                ActivityCell.class,
                BucketItemCell.class,
                ThemeHeaderCell.class,
                SoldOutCell.class,

                BaseArrayListAdapter.class,
                UploadJob.class
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

    @Provides
    SimpleKeyValueStorage provideSimpleKeyValueStorage(SharedPreferences preferences) {
        return new SimpleKeyValueStorage(preferences);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

}
