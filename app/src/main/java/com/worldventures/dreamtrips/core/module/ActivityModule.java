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
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceService;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.MembershipVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoriesListPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsFragmentPresenter;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.BookItDialogPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailTripActivityPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DetailedTripPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.DreamTripsFragmentPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.FiltersPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.FragmentMapInfoPresenter;
import com.worldventures.dreamtrips.modules.trips.presenter.MapFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMyPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.api.UploadTripPhotoCommand;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.auth.presenter.LoginFragmentPresenter;
import com.worldventures.dreamtrips.modules.auth.view.LoginActivity;
import com.worldventures.dreamtrips.modules.auth.view.LoginFragment;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListEditActivityPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPopularPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListQuickInputPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsFragmentPM;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketHeaderCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketQuickCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopuralFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListQuickInputFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.ShareActivityPM;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.SimpleStreamPlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.adapter.MyDraggableSwipeableItemAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerFragment;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumFragmentPM;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoFragmentPM;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPickPhotoActivityPM;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookAlbumItem;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookPhotoItem;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;
import com.worldventures.dreamtrips.modules.infopages.presenter.ActualTokenStaticInfoFragmentPM;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollActivityPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.Video360FragmentPM;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.activity.EnrollActivity;
import com.worldventures.dreamtrips.modules.infopages.view.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.infopages.view.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.infopages.view.cell.VideoCell;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.MemberShipFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.Video360Fragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.ActualTokenStaticInfoFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.fragment.ProfileFragment;
import com.worldventures.dreamtrips.modules.reptools.view.activity.SuccessStoryDetailsActivity;
import com.worldventures.dreamtrips.modules.reptools.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.RepToolsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesDetailsFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesListFragment;
import com.worldventures.dreamtrips.modules.trips.presenter.BookItActivityPresenter;
import com.worldventures.dreamtrips.modules.trips.view.activity.BookItActivity;
import com.worldventures.dreamtrips.modules.trips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.modules.trips.view.cell.ActivityCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.DateCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.FiltersCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.RegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.SoldOutCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.ThemeHeaderCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;
import com.worldventures.dreamtrips.modules.trips.view.dialog.BookItDialogFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DetailedTripFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.DreamTripsFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FiltersFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FragmentMapTripInfo;
import com.worldventures.dreamtrips.modules.trips.view.fragment.MapFragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoActivityPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoFragmentPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.DetailedImagePagerFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.InspireMePM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MyImagesPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.UserImagesPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.YSBHPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSViewPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSInspireMePM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSPhotoPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenActivityPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.ImageUploadTaskPM;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.FullScreenTripImageActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoUploadCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.DetailedImagePagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabsFragment;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                Presenter.class,
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
                SuccessStoryDetailsActivity.class,
                SuccessStoryDetailsPresenter.class,
                LaunchActivityPresenter.class,
                BucketListQuickInputPM.class,
                LoginFragmentPresenter.class,
                WebViewFragmentPresenter.class,
                ActivityPresenter.class,
                ProfilePresenter.class,
                MainActivityPresenter.class,
                FacebookPickPhotoActivityPM.class,
                NavigationDrawerAdapter.class,
                TripImagesTabsFragmentPresenter.class,
                TripImagesListPM.class,
                InspireMePM.class,
                MyImagesPM.class,
                UserImagesPM.class,
                YSBHPM.class,
                DreamTripsFragmentPresenter.class,
                DetailedTripPresenter.class,
                DetailTripActivityPresenter.class,
                FullScreenActivityPM.class,
                FullScreenPhotoFragment.class,
                CreatePhotoActivityPM.class,
                CreatePhotoFragmentPM.class,
                FacebookAlbumFragmentPM.class,
                FacebookPhotoFragmentPM.class,
                EnrollActivity.class,
                EnrollActivityPresenter.class,
                BucketPopularTabsFragmentPM.class,
                FiltersPresenter.class,
                BucketListQuickInputFragment.class,
                DetailedImagePagerFragmentPresenter.class,
                FragmentMapInfoPresenter.class,
                BookItDialogPresenter.class,

                NavigationDrawerFragment.class,
                FiltersFragment.class,
                MembershipVideosPresenter.class,
                BucketTabsFragmentPM.class,
                BucketListPresenter.class,
                MapFragmentPresenter.class,
                FSPhotoPM.class,
                Video360FragmentPM.class,
                FSInspireMePM.class,
                BucketListEditActivity.class,
                BucketListEditActivityPM.class,
                FSViewPM.class,
                BucketListPopularPM.class,
                ImageUploadTaskPM.class,
                ShareActivityPM.class,

                NavigationDrawerFragment.class,
                FragmentMapTripInfo.class,
                MemberShipFragment.class,
                ProfileFragment.class,
                BookItActivityPresenter.class,
                DreamTripsFragment.class,
                StaticInfoFragment.EnrollFragment.class,
                DetailedTripFragment.class,
                TripImagesTabsFragment.class,
                LoginFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                StaticInfoFragment.class,
                Video360Fragment.class,
                FacebookAlbumFragment.class,
                BucketListPopuralFragment.class,
                FacebookPhotoFragment.class,
                StaticInfoFragment.BookIt.class,
                StaticInfoFragment.BundleUrlFragment.class,
                StaticInfoFragment.TermsOfServiceFragment.class,
                StaticInfoFragment.PrivacyPolicyFragment.class,
                StaticInfoFragment.CookiePolicyFragment.class,
                StaticInfoFragment.FAQFragment.class,
                BookItDialogFragment.class,
                BucketTabsFragment.class,
                BucketPopularTabsFragment.class,
                BucketListFragment.class,
                DetailedImagePagerFragment.class,
                MapFragment.class,
                SuccessStoriesDetailsFragment.class,

                CreatePhotoFragment.class,
                FacebookAlbumItem.class,
                FacebookPhotoItem.class,
                NavigationDrawerPresenter.class,
                RegionCell.class,
                TripCell.class,
                BucketHeaderCell.class,
                PhotoCell.class,
                PhotoUploadCell.class,
                FiltersCell.class,
                VideoCell.class,
                Video360Cell.class,
                ActivityCell.class,
                BucketItemCell.class,
                ThemeHeaderCell.class,
                SoldOutCell.class,
                DateCell.class,
                Video360SmallCell.class,
                BucketQuickCell.class,
                BucketPopularCell.class,
                RepToolsFragment.class,
                RepToolsPresenter.class,
                SuccessStoryCell.class,
                SuccessStoriesListFragment.class,
                SuccessStoriesListPresenter.class,
                SuccessStoryDetailsFragmentPresenter.class,

                BaseArrayListAdapter.class,
                MyDraggableSwipeableItemAdapter.class,
                FilterableArrayListAdapter.class,
                GetMyPhotosQuery.class,
                DreamSpiceService.class,
                DreamSpiceManager.class,

                LoaderRecycleAdapter.class,
                IRoboSpiceAdapter.class,
                SimpleStreamPlayerActivity.class,
                UploadTripPhotoCommand.class,
                OtaFragment.class,
                ActualTokenStaticInfoFragment.class,
                ActualTokenStaticInfoFragmentPM.class,
                StaticInfoFragment.TrainingVideosFragment.class
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
    UniversalImageLoader provideImageLoader() {
        return new UniversalImageLoader();
    }

    @Provides
    FragmentCompass provideFragmentCompass() {
        return new FragmentCompass(baseActivity, R.id.container);
    }

    @Provides
    @Named("details")
    FragmentCompass provideFragmentCompassDetails() {
        return new FragmentCompass(baseActivity, R.id.detail_container);
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
}
