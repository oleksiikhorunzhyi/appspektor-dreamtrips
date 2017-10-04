package com.worldventures.dreamtrips.social.ui.tripsimages;

import com.messenger.ui.fragment.MessageImageFullscreenFragment;
import com.messenger.ui.fragment.PhotoAttachmentPagerFragment;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketFullscreenPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.activity.Player360Activity;
import com.worldventures.dreamtrips.modules.trips.presenter.TripImagePagerPresenter;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripImagePagerFragment;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripPhotoFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.EditPhotoTagsPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.FullscreenPhotoPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.FullscreenVideoPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.MemberImagesPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.TripImagesPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.TripImagesTabPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.TripImagesViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me.FullscreenInspireMePresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me.InspireMePresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me.InspireMeViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.FullscreenYsbhPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.YouShouldBeHerePresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.YsbhViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.EditPhotoTagsFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.FullscreenPhotoFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.FullscreenVideoFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.MemberImagesFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesTabFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.FullscreenInspireMeFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.InspireMeFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me.InspireMeViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.FullscreenYsbhFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.YouShouldBeHereFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.YsbhViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.video.presenter.ThreeSixtyVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.view.ThreeSixtyVideosFragment;
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            Player360Activity.class,
            ThreeSixtyVideosFragment.class,
            ThreeSixtyVideosPresenter.class,

            TripImagesTabFragment.class,
            TripImagesTabPresenter.class,
            TripImagesFragment.class,
            TripImagesPresenter.class,
            TripImagesFullscreenFragment.class,
            TripImagesViewPagerPresenter.class,
            MemberImagesFragment.class,
            MemberImagesPresenter.class,
            FullscreenVideoFragment.class,
            FullscreenVideoPresenter.class,
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

            VideoView.class,
      },
      complete = false,
      library = true)
public class TripImagesModule {

   @Provides
   @Singleton
   SocialViewPagerState socialViewPagerState() {
      return new SocialViewPagerState();
   }

}