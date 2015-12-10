package com.worldventures.dreamtrips.modules.tripsimages;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMembersPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.UserImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoParentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.InspireMePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.PhotoEditPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.MemberImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.YSBHPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.BucketFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.InspirationFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.SocialImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.TripImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.YouShouldBeHerePhotoFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoUploadCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.AccountImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenTripImageFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.PhotoEditFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.UsersImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.InspirePhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.SocialImageFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.TripPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.YSBHPhotoFullscreenFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                CreatePhotoActivity.class,
                TripImagesTabsPresenter.class,
                TripImagesListPresenter.class,
                InspireMePresenter.class,
                UserImagesPresenter.class,
                MemberImagesPresenter.class,
                YSBHPresenter.class,
                FullScreenPhotoFragment.class,
                CreatePhotoParentPresenter.class,
                CreatePhotoPresenter.class,
                TripImagePresenter.class,

                FullScreenPresenter.class,
                BucketFullscreenPresenter.class,
                InspirationFullscreenPresenter.class,

                TripImagesTabsFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                TripImagePagerFragment.class,
                PhotoEditFragment.class,
                PhotoEditPresenter.class,
                FullScreenTripImageFragment.class,
                CreatePhotoFragment.class,
                PhotoCell.class,
                PhotoUploadCell.class,

                GetMembersPhotosQuery.class,
                FullScreenPhotoWrapperFragment.class,
                GetUserPhotosQuery.class,
                AccountImagesListFragment.class,
                UsersImagesListFragment.class,

                InspirePhotoFullscreenFragment.class,
                YSBHPhotoFullscreenFragment.class,
                YouShouldBeHerePhotoFullscreenPresenter.class,
                TripPhotoFullscreenFragment.class,
                BucketPhotoFullscreenFragment.class,
                SocialImageFullscreenPresenter.class,
                SocialImageFullscreenFragment.class,

                InspirationFullscreenPresenter.class,
                TripImageFullscreenPresenter.class


        },
        complete = false,
        library = true
)
public class TripsImagesModule {

    public static final String TRIP_IMAGES = Route.TAB_IMAGES.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTripImagesComponent() {
        return new ComponentDescription(TRIP_IMAGES, R.string.trip_images, R.string.trip_images, R.drawable.ic_trip_images, TripImagesTabsFragment.class);
    }
}
