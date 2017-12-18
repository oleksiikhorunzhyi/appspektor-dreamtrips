package com.worldventures.dreamtrips.social.ui.feed;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.modules.picker.util.CapturedRowMediaHelper;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.media_picker.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.media_picker.view.fragment.DtGalleryFragment;
import com.worldventures.dreamtrips.social.ui.activity.FeedActivity;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.CreateEntityPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.DescriptionCreatorPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.EditPhotoPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.EditPostPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedListAdditionalInfoPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.HashtagFeedPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.LocationPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.PhotoStripDelegate;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.BucketFeedEntityDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.BucketFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.FeedEntityDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.FeedItemCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.PhotoFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.PhotoPostCreationCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.PostFeedItemCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.PostFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.TripFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.UndefinedFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.VideoFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.uploading.UploadingPostsSectionCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedViewInjector;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.PhotoStripView;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CommentableFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CreateEntityFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.DescriptionCreatorFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.EditCommentFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.EditPhotoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.EditPostFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedDetailsFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityDetailsFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedItemAdditionalInfoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedListAdditionalInfoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.HashtagFeedFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.LocationFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FeedAspectRatioHelper;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;

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
            PhotoStripView.class,
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
         TripImagesInteractor tripImagesInteractor, PostsInteractor postsInteractor, BucketInteractor bucketInteractor,
         AnalyticsInteractor analyticsInteractor, PermissionDispatcher permissionDispatcher) {
      return new FeedActionHandlerDelegate(feedInteractor, flagsInteractor, tripImagesInteractor, postsInteractor,
            bucketInteractor, analyticsInteractor, permissionDispatcher);
   }

   @Provides
   @Singleton
   FeedEntityHolderDelegate provideFeedItemsUpdateDelegate(TripImagesInteractor tripImagesInteractor,
         FeedInteractor feedInteractor, PostsInteractor postsInteractor, BucketInteractor bucketInteractor,
         FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {

      return new FeedEntityHolderDelegate(tripImagesInteractor, feedInteractor, postsInteractor,
            bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Provides
   FeedAspectRatioHelper provideFeedAspectRatioHelper() {
      return new FeedAspectRatioHelper();
   }

   @Provides
   SuggestedPhotoCellPresenterHelper provideSuggestedPhotoCellPresenterHelper(SessionHolder appSessionHolder,
         MediaPickerInteractor mediaInteractor) {
      return new SuggestedPhotoCellPresenterHelper(appSessionHolder, mediaInteractor);
   }

   @Provides
   PhotoStripDelegate providePhotoStripDelegate(@ForActivity Injector injector, MediaPickerInteractor mediaInteractor,
         AppConfigurationInteractor appConfigurationInteractor, PickImageDelegate pickImageDelegate,
         CapturedRowMediaHelper capturedRowMediaHelper, PermissionDispatcher permissionDispatcher, PermissionUtils permissionUtils) {
      return new PhotoStripDelegate(injector, mediaInteractor, appConfigurationInteractor, pickImageDelegate, capturedRowMediaHelper,
            permissionDispatcher, permissionUtils);
   }
}
