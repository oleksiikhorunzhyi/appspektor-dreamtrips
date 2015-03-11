package com.worldventures.dreamtrips.core.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.InjectingServiceModule;
import com.techery.spares.module.Injector;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.core.uploader.Logger;
import com.worldventures.dreamtrips.core.uploader.UploadingAPI;
import com.worldventures.dreamtrips.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.presentation.BookItActivityPresentation;
import com.worldventures.dreamtrips.presentation.BookItDialogPM;
import com.worldventures.dreamtrips.presentation.BucketListEditActivityPM;
import com.worldventures.dreamtrips.presentation.BucketListFragmentPM;
import com.worldventures.dreamtrips.presentation.BucketListPopularPM;
import com.worldventures.dreamtrips.presentation.BucketListQuickInputPM;
import com.worldventures.dreamtrips.presentation.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.presentation.CreatePhotoActivityPM;
import com.worldventures.dreamtrips.presentation.CreatePhotoFragmentPM;
import com.worldventures.dreamtrips.presentation.DetailTripActivityPM;
import com.worldventures.dreamtrips.presentation.DetailedImagePagerFragmentPresentation;
import com.worldventures.dreamtrips.presentation.DetailedTripFragmentPM;
import com.worldventures.dreamtrips.presentation.DreamTripsFragmentPM;
import com.worldventures.dreamtrips.presentation.DummyPresentationModel;
import com.worldventures.dreamtrips.presentation.EnrollActivityPresentation;
import com.worldventures.dreamtrips.presentation.FacebookAlbumFragmentPM;
import com.worldventures.dreamtrips.presentation.FacebookPhotoFragmentPM;
import com.worldventures.dreamtrips.presentation.FacebookPickPhotoActivityPM;
import com.worldventures.dreamtrips.presentation.FiltersFragmentPM;
import com.worldventures.dreamtrips.presentation.FragmentMapInfoPM;
import com.worldventures.dreamtrips.presentation.FullScreenActivityPM;
import com.worldventures.dreamtrips.presentation.LaunchActivityPresentation;
import com.worldventures.dreamtrips.presentation.LoginActivityPresentation;
import com.worldventures.dreamtrips.presentation.LoginFragmentPresentation;
import com.worldventures.dreamtrips.presentation.MainActivityPresentation;
import com.worldventures.dreamtrips.presentation.MapFragmentPM;
import com.worldventures.dreamtrips.presentation.MembershipPM;
import com.worldventures.dreamtrips.presentation.NavigationDrawerPM;
import com.worldventures.dreamtrips.presentation.ProfileFragmentPresentation;
import com.worldventures.dreamtrips.presentation.RepToolsFragmentPM;
import com.worldventures.dreamtrips.presentation.ShareActivityPM;
import com.worldventures.dreamtrips.presentation.SuccessStoresListFragmentPM;
import com.worldventures.dreamtrips.presentation.SuccessStoryDetailsPM;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;
import com.worldventures.dreamtrips.presentation.TripImagesTabsFragmentPresentation;
import com.worldventures.dreamtrips.presentation.WebViewFragmentPresentation;
import com.worldventures.dreamtrips.presentation.fullscreen.BaseFSViewPM;
import com.worldventures.dreamtrips.presentation.fullscreen.FSInspireMePM;
import com.worldventures.dreamtrips.presentation.fullscreen.FSPhotoPM;
import com.worldventures.dreamtrips.presentation.fullscreen.ImageUploadTaskPM;
import com.worldventures.dreamtrips.presentation.tripimages.InspireMePM;
import com.worldventures.dreamtrips.presentation.tripimages.MyImagesPM;
import com.worldventures.dreamtrips.presentation.tripimages.UserImagesPM;
import com.worldventures.dreamtrips.presentation.tripimages.YSBHPM;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.view.activity.EnrollActivity;
import com.worldventures.dreamtrips.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.activity.FullScreenTripImageActivity;
import com.worldventures.dreamtrips.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.view.activity.ShareActivity;
import com.worldventures.dreamtrips.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.view.adapter.MyDraggableSwipeableItemAdapter;
import com.worldventures.dreamtrips.view.adapter.item.PhotoItem;
import com.worldventures.dreamtrips.view.cell.ActivityCell;
import com.worldventures.dreamtrips.view.cell.BucketHeaderCell;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.view.cell.BucketQuickCell;
import com.worldventures.dreamtrips.view.cell.DateCell;
import com.worldventures.dreamtrips.view.cell.FiltersCell;
import com.worldventures.dreamtrips.view.cell.PhotoCell;
import com.worldventures.dreamtrips.view.cell.PhotoUploadCell;
import com.worldventures.dreamtrips.view.cell.RegionCell;
import com.worldventures.dreamtrips.view.cell.SoldOutCell;
import com.worldventures.dreamtrips.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.view.cell.ThemeHeaderCell;
import com.worldventures.dreamtrips.view.cell.TripCell;
import com.worldventures.dreamtrips.view.cell.VideoCell;
import com.worldventures.dreamtrips.view.dialog.BookItDialogFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookAlbumItem;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookPhotoItem;
import com.worldventures.dreamtrips.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.view.fragment.BucketListPopuralFragment;
import com.worldventures.dreamtrips.view.fragment.BucketListQuickInputFragment;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.view.fragment.DetailedImagePagerFragment;
import com.worldventures.dreamtrips.view.fragment.DetailedTripFragment;
import com.worldventures.dreamtrips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.view.fragment.FragmentMapTripInfo;
import com.worldventures.dreamtrips.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.LoginFragment;
import com.worldventures.dreamtrips.view.fragment.MapFragment;
import com.worldventures.dreamtrips.view.fragment.MemberShipFragment;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.view.fragment.reptools.RepToolsFragment;
import com.worldventures.dreamtrips.view.fragment.reptools.SuccessStoresDetails;
import com.worldventures.dreamtrips.view.fragment.reptools.SuccessStoresListFragment;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        injects = {
                LaunchActivity.class,
                MainActivity.class,
                LoginActivity.class,
                PlayerActivity.class,
                BookItActivity.class,
                FullScreenPhotoActivity.class,
                FullScreenTripImageActivity.class,
                ShareActivity.class,
                DetailTripActivity.class,
                FBPickPhotoActivity.class,
                CreatePhotoActivity.class,
                LaunchActivityPresentation.class,
                BucketListQuickInputPM.class,
                LoginActivityPresentation.class,
                LoginFragmentPresentation.class,
                WebViewFragmentPresentation.class,
                BaseActivityPresentation.class,
                ProfileFragmentPresentation.class,
                MainActivityPresentation.class,
                FacebookPickPhotoActivityPM.class,
                NavigationDrawerAdapter.class,
                TripImagesTabsFragmentPresentation.class,
                TripImagesListPM.class,
                InspireMePM.class,
                MyImagesPM.class,
                UserImagesPM.class,
                YSBHPM.class,
                DreamTripsFragmentPM.class,
                DetailedTripFragmentPM.class,
                DetailTripActivityPM.class,
                FullScreenActivityPM.class,
                FullScreenPhotoFragment.class,
                CreatePhotoActivityPM.class,
                CreatePhotoFragmentPM.class,
                DummyPresentationModel.class,
                FacebookAlbumFragmentPM.class,
                FacebookPhotoFragmentPM.class,
                EnrollActivity.class,
                EnrollActivityPresentation.class,
                FiltersFragmentPM.class,
                BucketListQuickInputFragment.class,
                DetailedImagePagerFragmentPresentation.class,
                FragmentMapInfoPM.class,
                BookItDialogPM.class,

                NavigationDrawerFragment.class,
                FiltersFragment.class,
                MembershipPM.class,
                BucketTabsFragmentPM.class,
                BucketListFragmentPM.class,
                MapFragmentPM.class,
                FSPhotoPM.class,
                FSInspireMePM.class,
                BucketListEditActivity.class,
                BucketListEditActivityPM.class,
                BaseFSViewPM.class,
                BucketListPopularPM.class,
                ImageUploadTaskPM.class,
                ShareActivityPM.class,

                NavigationDrawerFragment.class,
                FragmentMapTripInfo.class,
                MemberShipFragment.class,
                ProfileFragment.class,
                BookItActivityPresentation.class,
                DreamTripsFragment.class,
                StaticInfoFragment.EnrollFragment.class,
                DetailedTripFragment.class,
                TripImagesTabsFragment.class,
                LoginFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                StaticInfoFragment.class,
                FacebookAlbumFragment.class,
                BucketListPopuralFragment.class,
                FacebookPhotoFragment.class,
                StaticInfoFragment.BundleUrlFragment.class,
                StaticInfoFragment.TermsOfServiceFragment.class,
                StaticInfoFragment.PrivacyPolicyFragment.class,
                StaticInfoFragment.CookiePolicyFragment.class,
                StaticInfoFragment.FAQFragment.class,
                BookItDialogFragment.class,
                BucketTabsFragment.class,
                BucketListFragment.class,
                DetailedImagePagerFragment.class,
                MapFragment.class,
                SuccessStoresDetails.class,

                CreatePhotoFragment.class,
                FacebookAlbumItem.class,
                FacebookPhotoItem.class,
                NavigationDrawerPM.class,
                RegionCell.class,
                TripCell.class,
                PhotoItem.class,
                BucketHeaderCell.class,
                PhotoCell.class,
                PhotoUploadCell.class,
                FiltersCell.class,
                VideoCell.class,
                ActivityCell.class,
                BucketItemCell.class,
                ThemeHeaderCell.class,
                SoldOutCell.class,
                DateCell.class,
                BucketQuickCell.class,
                BucketPopularCell.class,
                RepToolsFragment.class,
                RepToolsFragmentPM.class,
                SuccessStoryCell.class,
                SuccessStoresListFragment.class,
                SuccessStoresListFragmentPM.class,
                SuccessStoryDetailsPM.class,

                BaseArrayListAdapter.class,
                MyDraggableSwipeableItemAdapter.class,
                FilterableArrayListAdapter.class,
                DreamTripsRequest.GetMyPhotos.class,
                DreamSpiceService.class,
                DreamSpiceManager.class,

                LoaderRecycleAdapter.class,
                IRoboSpiceAdapter.class,
                DreamTripsRequest.UploadTripPhoto.class
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
        return new FragmentCompass(baseActivity, R.id.container);
    }

    @Provides
    @Named("details")
    public FragmentCompass provideFragmentCompassDetails() {
        return new FragmentCompass(baseActivity, R.id.detail_container);
    }

    @Provides
    SimpleKeyValueStorage provideSimpleKeyValueStorage(SharedPreferences preferences) {
        return new SimpleKeyValueStorage(preferences);
    }

    @Provides
    @Singleton
    BucketListSelectionStorage provideBucketListSelectionStorage(SimpleKeyValueStorage simpleKeyValueStorage, AppSessionHolder appSessionHolder) {
        return new BucketListSelectionStorage(simpleKeyValueStorage, appSessionHolder.get().get().getUsername());
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }


    @Provides
    DreamSpiceManager provideSpiceManager(BaseApplicationWithInjector injector) {
        return new DreamSpiceManager(DreamSpiceService.class, injector);
    }


    @Provides
    DependencyInjector provideDependencyInjector(@InjectingServiceModule.Service Injector injector) {
        return injector::inject;
    }

    @Provides
    Configuration provideJobManagerConfiguration(Context context, DependencyInjector injector) {
        return new Configuration.Builder(context)
                .customLogger(new Logger())
                .injector(injector)
                .minConsumerCount(1)
                .maxConsumerCount(5)
                .loadFactor(3)
                .consumerKeepAlive(15)
                .id("Uploading Job Manager")
                .build();
    }

    @Provides
    @Singleton
    JobManager provideJobManager(Context context, Configuration configuration) {
        return new JobManager(context, configuration);
    }


    @Provides
    UploadingAPI provideUploadingAPI(RestAdapter adapter) {
        return adapter.create(UploadingAPI.class);
    }

}
