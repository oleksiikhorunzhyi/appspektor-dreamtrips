package com.worldventures.dreamtrips.modules.tripsimages;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMyPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.api.UploadTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoActivityPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.CreatePhotoFragmentPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.DetailedImagePagerFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.InspireMePM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MyImagesPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.UserImagesPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.YSBHPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSInspireMePM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSPhotoPM;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FSViewPM;
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

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                FullScreenPhotoActivity.class,
                FullScreenTripImageActivity.class,
                CreatePhotoActivity.class,
                TripImagesTabsFragmentPresenter.class,
                TripImagesListPM.class,
                InspireMePM.class,
                MyImagesPM.class,
                UserImagesPM.class,
                YSBHPM.class,
                FullScreenActivityPM.class,
                FullScreenPhotoFragment.class,
                CreatePhotoActivityPM.class,
                CreatePhotoFragmentPM.class,
                DetailedImagePagerFragmentPresenter.class,

                FSPhotoPM.class,
                FSInspireMePM.class,
                FSViewPM.class,
                ImageUploadTaskPM.class,

                TripImagesTabsFragment.class,
                TripImagesListFragment.class,
                TripImagesTabsFragment.class,
                DetailedImagePagerFragment.class,

                CreatePhotoFragment.class,
                PhotoCell.class,
                PhotoUploadCell.class,

                UploadTripPhotoCommand.class,
                GetMyPhotosQuery.class,
        },
        complete = false,
        library = true
)
public class TripsImagesModule {

    public static final String TRIP_IMAGES = "trip_images";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTripImagesComponent() {
        return new ComponentDescription(TRIP_IMAGES, R.string.trip_images, R.drawable.ic_trip_images, TripImagesTabsFragment.class);
    }
}
