package com.worldventures.dreamtrips.modules.feed;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.DtGalleryFragment;
import com.worldventures.dreamtrips.modules.feed.presenter.ActionEntityPresenter;
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
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.HashtagSuggestionCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoPostCreationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PickerIrregularPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostCreationTextCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SubPhotoAttachmentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestionPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.TripFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.UndefinedFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.UploadingPhotoPostsSectionCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.FeedViewInjector;
import com.worldventures.dreamtrips.modules.feed.view.fragment.ActionEntityFragment;
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
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreateTripImageFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {TripFeedItemDetailsCell.class,
            FeedPresenter.class,
            SuggestedPhotoCellPresenterHelper.class,
            FeedFragment.class,
            BucketFeedItemDetailsCell.class,
            LoadMoreCell.class,
            PhotoFeedItemDetailsCell.class,
            PostFeedItemCell.class,
            UndefinedFeedItemDetailsCell.class,
            HashtagFeedFragment.class,
            HashtagFeedPresenter.class,
            PickerIrregularPhotoCell.class,
            PhotoGalleryCell.class,
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
            NotificationFragment.NotificationAdapter.class,
            FeedItemDetailsCell.class,
            FeedItemCell.class,
            BaseFeedCell.class,
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
            ActionEntityFragment.class,
            ActionEntityPresenter.class,
            CreateTripImageFragment.class,
            CreateFeedPostFragment.class,
            CreateEntityFragment.class,
            CreateEntityPresenter.class,
            LocationFragment.class,
            LocationPresenter.class,
            SuggestedPhotosCell.class,
            SuggestionPhotoCell.class,
            PhotoPostCreationCell.class,
            PostCreationTextCell.class,
            SubPhotoAttachmentCell.class,
            PostFeedItemDetailsCell.class,
            EditPostFragment.class,
            EditPostPresenter.class,
            EditPhotoFragment.class,
            EditPhotoPresenter.class,
            DescriptionCreatorFragment.class,
            DescriptionCreatorPresenter.class,
            HashtagSuggestionCell.class,
            StatePaginatedRecyclerViewManager.class,
            UploadingPhotoPostsSectionCell.class,
      },
      complete = false,
      library = true)
public class FeedModule {
   public static final String FEED = Route.FEED.name();
   public static final String NOTIFICATIONS = Route.NOTIFICATIONS.name();

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideFeedComponent() {
      return new ComponentDescription(FEED, R.string.feed_title, R.string.feed_title, R.drawable.ic_feed, FeedFragment.class);
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideNotificationComponent() {
      return new ComponentDescription(NOTIFICATIONS, R.string.notifications_title, R.string.notifications_title, R.drawable.ic_notifications, NotificationFragment.class);
   }

   @Provides
   FeedEntityContentFragmentFactory provideFeedEntityContentFragmentFactory(SessionHolder<UserSession> sessionHolder) {
      return new FeedEntityContentFragmentFactory(sessionHolder);
   }

   @Provides
   FragmentWithFeedDelegate provideFragmentWithFeedDelegate(Router router) {
      return new FragmentWithFeedDelegate(router);
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
}
