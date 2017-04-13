package com.worldventures.dreamtrips.modules.tripsimages;

import com.messenger.ui.fragment.MessageImageFullscreenFragment;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.AccountImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.EditPhotoTagsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.InspireMePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MembersImagesBasePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MembersImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.YSBHPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.BucketFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.InspirationFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.SocialImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.TripImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.YouShouldBeHerePhotoFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoUploadCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.AccountImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.EditPhotoTagsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenTripImageFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.MembersImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagePagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.InspirePhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.SocialImageFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.TripPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.YSBHPhotoFullscreenFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {TripImagesTabsPresenter.class,
            TripImagesListPresenter.class,
            InspireMePresenter.class,
            AccountImagesPresenter.class,
            MembersImagesPresenter.class,
            MembersImagesBasePresenter.class,
            YSBHPresenter.class, 
            FullScreenPhotoFragment.class,
            TripImagePresenter.class,
            FullScreenPresenter.class,
            BucketFullscreenPresenter.class,
            InspirationFullscreenPresenter.class,
            TripImagesTabsFragment.class,
            TripImagesListFragment.class,
            TripImagesTabsFragment.class,
            TripImagePagerFragment.class,
            FullScreenTripImageFragment.class,
            PhotoCell.class,
            PhotoUploadCell.class,
            FullScreenPhotoWrapperFragment.class,
            AccountImagesListFragment.class,
            MembersImagesListFragment.class,
            InspirePhotoFullscreenFragment.class,
            YSBHPhotoFullscreenFragment.class,
            YouShouldBeHerePhotoFullscreenPresenter.class,
            TripPhotoFullscreenFragment.class,
            BucketPhotoFullscreenFragment.class,
            SocialImageFullscreenPresenter.class,
            SocialImageFullscreenFragment.class,
            InspirationFullscreenPresenter.class,
            TripImageFullscreenPresenter.class,
            MessageImageFullscreenFragment.class,
            MessageImageFullscreenPresenter.class,
            EditPhotoTagsPresenter.class,
            EditPhotoTagsFragment.class,},
      complete = false,
      library = true)
public class TripsImagesModule {

   public static final String TRIP_IMAGES = Route.TRIP_TAB_IMAGES.name();

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTripImagesComponent() {
      return new ComponentDescription.Builder()
            .key(TRIP_IMAGES)
            .navMenuTitle(R.string.trip_images)
            .toolbarTitle(R.string.trip_images)
            .icon(R.drawable.ic_trip_images)
            .fragmentClass(TripImagesTabsFragment.class)
            .build();
   }
}
