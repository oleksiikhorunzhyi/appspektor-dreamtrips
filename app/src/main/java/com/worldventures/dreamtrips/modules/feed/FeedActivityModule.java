package com.worldventures.dreamtrips.modules.feed;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.PickImageDelegate;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.DescriptionCreatorPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditPhotoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditPostPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedListAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.HashtagFeedPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.LocationPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.PhotoStripDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.social.ui.activity.FeedActivity;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoPostCreationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.TripFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.UndefinedFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.VideoFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.UploadingPostsSectionCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.FeedViewInjector;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentableFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateEntityFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateFeedPostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.DescriptionCreatorFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditCommentFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditPhotoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditPostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedListAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.HashtagFeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.LocationFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedAspectRatioHelper;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.media_picker.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.media_picker.util.CapturedRowMediaHelper;
import com.worldventures.dreamtrips.modules.media_picker.view.fragment.DtGalleryFragment;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            FeedActivity.class,
            TripFeedItemDetailsCell.class,
            FeedPresenter.class,
            SuggestedPhotoCellPresenterHelper.class,
            FeedFragment.class,
            BucketFeedItemDetailsCell.class,
            PhotoFeedItemDetailsCell.class,
            PostFeedItemCell.class,
            UndefinedFeedItemDetailsCell.class,
            HashtagFeedFragment.class,
            HashtagFeedPresenter.class,
            EditCommentPresenter.class,
            CommentableFragment.class,
            ComponentPresenter.class,
            FeedItemDetailsFragment.class,
            FeedItemDetailsPresenter.class,
            FeedDetailsFragment.class,
            FeedDetailsPresenter.class,
            FeedEntityDetailsFragment.class,
            FeedEntityDetailsPresenter.class,
            CommentCell.class,
            BaseCommentPresenter.class,
            NotificationFragment.class,
            NotificationPresenter.class,
            NotificationCell.class,
            FeedItemCell.class,
            FeedEntityDetailsCell.class,
            BucketFeedEntityDetailsCell.class,
            TripFeedItemDetailsCell.class,
            FeedListAdditionalInfoFragment.class,
            FeedListAdditionalInfoPresenter.class,
            FeedItemAdditionalInfoFragment.class,
            FeedItemAdditionalInfoPresenter.class,
            EditCommentFragment.class,
            EditCommentPresenter.class,
            DtGalleryFragment.class,
            GalleryPresenter.class,
            CreateFeedPostFragment.class,
            CreateEntityFragment.class,
            CreateEntityPresenter.class,
            LocationFragment.class,
            LocationPresenter.class,
            SuggestedPhotosCell.class,
            PhotoPostCreationCell.class,
            PostFeedItemDetailsCell.class,
            EditPostFragment.class,
            EditPostPresenter.class,
            EditPhotoFragment.class,
            EditPhotoPresenter.class,
            DescriptionCreatorFragment.class,
            DescriptionCreatorPresenter.class,
            UploadingPostsSectionCell.class,
            VideoFeedItemDetailsCell.class,
      },
      complete = false,
      library = true)
public class FeedActivityModule {

   @Provides
   FeedEntityContentFragmentFactory provideFeedEntityContentFragmentFactory(SessionHolder sessionHolder) {
      return new FeedEntityContentFragmentFactory(sessionHolder);
   }

   @Provides
   FragmentWithFeedDelegate provideFragmentWithFeedDelegate(Router router, FeedAspectRatioHelper feedAspectRatioHelper) {
      return new FragmentWithFeedDelegate(router, feedAspectRatioHelper);
   }

   @Provides
   TranslationDelegate provideTextualPostTranslationDelegate(TranslationFeedInteractor translationFeedInteractor) {
      return new TranslationDelegate(translationFeedInteractor);
   }

   @Provides
   @Singleton
   FeedViewInjector provideFeedViewInjector(Context context) {
      return new FeedViewInjector(context);
   }

   @Provides
   @Singleton
   UploadingPresenterDelegate provideUploadingPresenterDelegate(BackgroundUploadingInteractor uploadingInteractor) {
      return new UploadingPresenterDelegate(uploadingInteractor);
   }

   @Provides
   @Singleton
   FeedActionHandlerDelegate provideFeedActionHandlerDelegate(FeedInteractor feedInteractor, FlagsInteractor flagsInteractor,
         TripImagesInteractor tripImagesInteractor, PostsInteractor postsInteractor, BucketInteractor bucketInteractor) {
      return new FeedActionHandlerDelegate(feedInteractor, flagsInteractor, tripImagesInteractor, postsInteractor,
            bucketInteractor);
   }

   @Provides
   @Singleton
   FeedEntityHolderDelegate provideFeedItemsUpdateDelegate(@ForApplication Injector injector) {
      return new FeedEntityHolderDelegate(injector);
   }

   @Provides
   FeedAspectRatioHelper provideFeedAspectRatioHelper() {
      return new FeedAspectRatioHelper();
   }

   @Provides
   SuggestedPhotoCellPresenterHelper provideSuggestedPhotoCellPresenterHelper(SessionHolder appSessionHolder,
         MediaInteractor mediaInteractor) {
      return new SuggestedPhotoCellPresenterHelper(appSessionHolder, mediaInteractor);
   }

   @Provides
   PhotoStripDelegate providePhotoStripDelegate(@ForActivity Injector injector, MediaInteractor mediaInteractor,
         AppConfigurationInteractor appConfigurationInteractor, PickImageDelegate pickImageDelegate,
         CapturedRowMediaHelper capturedRowMediaHelper, PermissionDispatcher permissionDispatcher) {
      return new PhotoStripDelegate(injector, mediaInteractor, appConfigurationInteractor, pickImageDelegate, capturedRowMediaHelper, permissionDispatcher);
   }
}
