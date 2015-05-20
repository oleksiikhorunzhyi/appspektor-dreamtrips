package com.worldventures.dreamtrips.modules.tripsimages;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMyPhotosBaseQuery;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMyPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoParentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.DetailedImagePagerFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.InspireMePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MyImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.UserImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.YSBHPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSInspireMePM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSPhotoPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenParentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
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

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                FullScreenPhotoActivity.class,
                FullScreenTripImageActivity.class,
                CreatePhotoActivity.class,
                TripImagesTabsFragmentPresenter.class,
                TripImagesListPresenter.class,
                InspireMePresenter.class,
                MyImagesPresenter.class,
                UserImagesPresenter.class,
                YSBHPM.class,
                FullScreenParentPresenter.class,
                FullScreenPhotoFragment.class,
                CreatePhotoParentPresenter.class,
                CreatePhotoPresenter.class,
                DetailedImagePagerFragmentPresenter.class,

                FSPhotoPresenter.class,
                FSInspireMePM.class,
                FullScreenPresenter.class,
                ImageUploadTaskPM.class,

                TripImagesTabsFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                DetailedImagePagerFragment.class,

                CreatePhotoFragment.class,
                PhotoCell.class,
                PhotoUploadCell.class,

                GetMyPhotosQuery.class,
                GetMyPhotosBaseQuery.class
        },
        complete = false,
        library = true
)
public class TripsImagesModule {

    public static final String TRIP_IMAGES = Route.TRIP_IMAGES.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTripImagesComponent() {
        return new ComponentDescription(TRIP_IMAGES, R.string.trip_images, R.drawable.ic_trip_images, TripImagesTabsFragment.class);
    }
}
