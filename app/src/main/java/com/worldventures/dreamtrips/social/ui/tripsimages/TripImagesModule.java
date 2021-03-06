package com.worldventures.dreamtrips.social.ui.tripsimages;

import com.messenger.ui.fragment.MessageImageFullscreenFragment;
import com.messenger.ui.fragment.PhotoAttachmentPagerFragment;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImagePresenter;
import com.worldventures.dreamtrips.social.ui.activity.Player360Activity;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketPhotoFullscreenPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketPhotoFullscreenFragment;
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
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.YSBHPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.YSBHViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.DownloadImageDelegate;
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
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.YSBHFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh.YsbhViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.video.presenter.ThreeSixtyVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.view.ThreeSixtyVideosFragment;

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

            YSBHFragment.class,
            YSBHPresenter.class,
            YsbhViewPagerFragment.class,
            YSBHViewPagerPresenter.class,
            FullscreenYsbhFragment.class,
            FullscreenYsbhPresenter.class,

            BaseImagePresenter.class,
            BaseImageFragment.class,

            PhotoAttachmentPagerFragment.class,
            PhotoAttachmentPagerFragment.Presenter.class,
            MessageImageFullscreenFragment.class,
            MessageImageFullscreenPresenter.class,

            BucketPhotoFullscreenPresenter.class,
            BucketPhotoFullscreenFragment.class,
            EditPhotoTagsPresenter.class,
            EditPhotoTagsFragment.class,
      },
      complete = false,
      library = true)
public class TripImagesModule {

   @Provides
   @Singleton
   SocialViewPagerState socialViewPagerState() {
      return new SocialViewPagerState();
   }

   @Provides
   DownloadImageDelegate provideDownloadImageDelegate(TripImagesInteractor tripImagesInteractor, PermissionDispatcher permissionDispatcher) {
      return new DownloadImageDelegate(tripImagesInteractor, permissionDispatcher);
   }

}
