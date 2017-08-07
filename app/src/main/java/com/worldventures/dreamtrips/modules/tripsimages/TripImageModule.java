package com.worldventures.dreamtrips.modules.tripsimages;

import com.messenger.ui.fragment.MessageImageFullscreenFragment;
import com.messenger.ui.fragment.PhotoAttachmentPagerFragment;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketFullscreenPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.EditPhotoTagsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.FullscreenPhotoPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MemberImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesViewPagerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.inspire_me.FullscreenInspireMePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.inspire_me.InspireMePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.inspire_me.InspireMeViewPagerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.ysbh.FullscreenYsbhPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.ysbh.YouShouldBeHerePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.ysbh.YsbhViewPagerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.InspirationPhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.TripImageCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.TripImageTimestampCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.YsbhPhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.EditPhotoTagsFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullscreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.MemberImagesFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesTabFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.inspire_me.FullscreenInspireMeFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.inspire_me.InspireMeFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.inspire_me.InspireMeViewPagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.ysbh.FullscreenYsbhFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.ysbh.YouShouldBeHereFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.ysbh.YsbhViewPagerFragment;
import com.worldventures.dreamtrips.modules.trips.presenter.TripImagePagerPresenter;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripImagePagerFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripPhotoFullscreenFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            TripImagesTabFragment.class,
            TripImagesTabPresenter.class,
            TripImagesFragment.class,
            TripImagesPresenter.class,
            TripImagesFullscreenFragment.class,
            TripImagesViewPagerPresenter.class,
            MemberImagesFragment.class,
            MemberImagesPresenter.class,
            FullscreenPhotoFragment.class,
            FullscreenPhotoPresenter.class,

            InspireMeFragment.class,
            InspireMePresenter.class,
            InspireMeViewPagerFragment.class,
            InspireMeViewPagerPresenter.class,
            FullscreenInspireMeFragment.class,
            FullscreenInspireMePresenter.class,

            YouShouldBeHereFragment.class,
            YouShouldBeHerePresenter.class,
            YsbhViewPagerFragment.class,
            YsbhViewPagerPresenter.class,
            FullscreenYsbhFragment.class,
            FullscreenYsbhPresenter.class,

            TripImageCell.class,
            YsbhPhotoCell.class,
            InspirationPhotoCell.class,
            TripImageTimestampCell.class,

            TripImagePagerPresenter.class,
            TripImagePagerFragment.class,

            PhotoAttachmentPagerFragment.class,
            PhotoAttachmentPagerFragment.Presenter.class,
            MessageImageFullscreenFragment.class,
            MessageImageFullscreenPresenter.class,

            BucketFullscreenPresenter.class,
            BucketPhotoFullscreenFragment.class,
            TripPhotoFullscreenFragment.class,
            EditPhotoTagsPresenter.class,
            EditPhotoTagsFragment.class,
      },
      complete = false,
      library = true)
public class TripImageModule {
   public static final String TRIP_IMAGES = Route.TRIP_TAB_IMAGES.name();

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTripImagesComponent() {
      return new ComponentDescription.Builder()
            .key(TRIP_IMAGES)
            .navMenuTitle(R.string.trip_images)
            .toolbarTitle(R.string.trip_images)
            .icon(R.drawable.ic_trip_images)
            .fragmentClass(TripImagesTabFragment.class)
            .build();
   }

   @Provides
   @Singleton
   SocialViewPagerState socialViewPagerState() {
      return new SocialViewPagerState();
   }

}
