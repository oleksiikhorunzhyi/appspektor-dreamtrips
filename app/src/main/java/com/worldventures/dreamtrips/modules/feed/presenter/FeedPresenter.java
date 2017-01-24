package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.utils.delegate.EntityDeletedEventDelegate;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.DownloadPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.event.TranslatePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.SuggestedPhotoInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.ViewFeedAction;
import com.worldventures.dreamtrips.modules.feed.service.command.BaseGetFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.view.util.TextualPostTranslationDelegate;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FeedPresenter extends Presenter<FeedPresenter.View> implements UploadingListenerPresenter {

   private static final int SUGGESTION_ITEM_CHUNK = 15;

   @Inject SnappyRepository db;
   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject TextualPostTranslationDelegate textualPostTranslationDelegate;
   @Inject DrawableUtil drawableUtil;
   @Inject UnreadConversationObservable unreadConversationObservable;
   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;
   @Inject EntityDeletedEventDelegate entityDeletedEventDelegate;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;

   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SuggestedPhotoInteractor suggestedPhotoInteractor;
   @Inject CirclesInteractor circlesInteractor;
   @Inject FlagsInteractor flagsInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   private Circle filterCircle;
   private FlagDelegate flagDelegate;
   private SuggestedPhotoCellPresenterHelper suggestedPhotoHelper;

   @State ArrayList<FeedItem> feedItems;
   private List<PostCompoundOperationModel> postUploads;
   private List<PhotoGalleryModel> suggestedPhotos;
   @State int unreadConversationCount;

   @Override
   public void onInjected() {
      super.onInjected();
      flagDelegate = new FlagDelegate(flagsInteractor);
   }

   @Override
   public void restoreInstanceState(Bundle savedState) {
      super.restoreInstanceState(savedState);
      if (savedState == null) feedItems = new ArrayList<>();
      filterCircle = db.getFilterCircle();
      if (filterCircle == null) filterCircle = createDefaultFilterCircle();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      updateCircles();
      subscribeRefreshFeeds();
      subscribeLoadNextFeeds();
      subscribePhotoGalleryCheck();
      subscribeUnreadConversationsCount();
      subscribeFriendsNotificationsCount();
      subscribeToLikesChanges();
      subscribeToEntityDeletedEvents();
      subscribeToBackgroundUploadingOperations();
      textualPostTranslationDelegate.onTakeView(view, feedItems);

      if (feedItems.size() != 0) {
         refreshFeedItemsInView();
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe().send(new ViewFeedAction());
   }


   @Override
   public void saveInstanceState(Bundle outState) {
      super.saveInstanceState(outState);
      if (suggestedPhotoHelper != null) {
         suggestedPhotoHelper.saveInstanceState(outState);
      }
   }

   @Override
   public void dropView() {
      textualPostTranslationDelegate.onDropView();
      super.dropView();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Update circles
   ///////////////////////////////////////////////////////////////////////////

   private Circle createDefaultFilterCircle() {
      return Circle.all(context.getString(R.string.all));
   }

   public void applyFilter(Circle selectedCircle) {
      filterCircle = selectedCircle;
      db.saveFilterCircle(selectedCircle);
      refreshFeed();
   }

   public void actionFilter() {
      circlesInteractor.pipe()
            .createObservable(new GetCirclesCommand())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> onCirclesSuccess(circlesCommand.getResult()))
                  .onFail(this::onCirclesError));
   }

   private void updateCircles() {
      circlesInteractor.pipe().send(new GetCirclesCommand());
   }

   private void onCirclesStart() {
      view.showBlockingProgress();
   }

   private void onCirclesSuccess(List<Circle> resultCircles) {
      resultCircles.add(createDefaultFilterCircle());
      Collections.sort(resultCircles);
      view.hideBlockingProgress();
      view.showFilter(resultCircles, filterCircle);
   }

   private void onCirclesError(CommandWithError commandWithError, Throwable throwable) {
      view.hideBlockingProgress();
      handleError(commandWithError, throwable);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Refresh feeds
   ///////////////////////////////////////////////////////////////////////////

   private void subscribeRefreshFeeds() {
      feedInteractor.getRefreshAccountFeedPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountFeedCommand.Refresh>()
                  .onSuccess(action -> refreshFeedSucceed(action.getResult()))
                  .onFail(this::refreshFeedError));
   }

   private void refreshFeedSucceed(List<FeedItem> freshItems) {
      boolean noMoreFeeds = freshItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
      //
      view.finishLoading();
      feedItems.clear();
      feedItems.addAll(freshItems);
      //
      suggestedPhotoInteractor.getSuggestedPhotoCommandActionPipe().send(new SuggestedPhotoCommand());
   }

   private void refreshFeedError(BaseGetFeedCommand action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
      view.finishLoading();
      refreshFeedItemsInView();
   }

   public void refreshFeed() {
      view.startLoading();
      feedInteractor.getRefreshAccountFeedPipe().send(new GetAccountFeedCommand.Refresh(filterCircle.getId()));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Load more feeds
   ///////////////////////////////////////////////////////////////////////////

   private void subscribeLoadNextFeeds() {
      feedInteractor.getLoadNextAccountFeedPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountFeedCommand.LoadNext>()
                  .onSuccess(action -> addFeedItems(action.getResult()))
                  .onFail(this::loadMoreItemsError));
   }

   private void addFeedItems(List<FeedItem> olderItems) {
      boolean noMoreFeeds = olderItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
      //
      feedItems.addAll(olderItems);
      refreshFeedItemsInView();
   }

   private void loadMoreItemsError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
      addFeedItems(new ArrayList<>());
   }

   public void loadNext() {
      if(feedItems.isEmpty()) {
         addFeedItems(feedItems);
         return;
      } else {
         feedInteractor.getLoadNextAccountFeedPipe()
               .send(new GetAccountFeedCommand.LoadNext(filterCircle.getId(), getLastFeedDate()));
      }
   }

   private Date getLastFeedDate() {
      return feedItems.get(feedItems.size() - 1).getCreatedAt();
   }

   public void onUnreadConversationsClick() {
      MessengerActivity.startMessenger(activityRouter.getContext());
   }

   public int getFriendsRequestsCount() {
      return db.getFriendsRequestsCount();
   }

   public int getUnreadConversationCount() {
      return unreadConversationCount;
   }

   public void onEvent(DownloadPhotoEvent event) {
      if (view.isVisibleOnScreen()) {
         tripImagesInteractor.downloadImageActionPipe()
               .createObservable(new DownloadImageCommand(event.url))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                     .onFail(this::handleError));
      }
   }

   public void onEvent(EditBucketEvent event) {
      if (!view.isVisibleOnScreen()) return;
      //
      BucketBundle bundle = new BucketBundle();
      bundle.setType(event.type());
      bundle.setBucketItem(event.bucketItem());

      view.showEdit(bundle);
   }

   public void onEvent(DeleteBucketEvent event) {
      if (view.isVisibleOnScreen()) {
         BucketItem item = event.getEntity();

         view.bind(bucketInteractor.deleteItemPipe()
               .createObservable(new DeleteBucketItemCommand(item.getUid()))
               .observeOn(AndroidSchedulers.mainThread()))
               .subscribe(new ActionStateSubscriber<DeleteBucketItemCommand>().onSuccess(deleteItemAction -> itemDeleted(item)));
      }
   }

   private void itemDeleted(FeedEntity feedEntity) {
      List<FeedItem> filteredItems = Queryable.from(feedItems)
            .filter(element -> !element.getItem().equals(feedEntity))
            .toList();

      feedItems.clear();
      feedItems.addAll(filteredItems);

      refreshFeedItemsInView();
   }

   private void subscribeToEntityDeletedEvents() {
      entityDeletedEventDelegate.getReplayObservable()
            .compose(bindViewToMainComposer())
            .subscribe(this::itemDeleted);
   }

   private void subscribeToBackgroundUploadingOperations() {
      backgroundUploadingInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess(compoundOperationsCommand -> {
                        postUploads = Queryable.from(compoundOperationsCommand.getResult()).cast(PostCompoundOperationModel.class).toList();
                        refreshFeedItemsInView();
                  }));
   }

   public void onEventMainThread(FeedItemAddedEvent event) {
      feedItems.add(0, event.getFeedItem());
      refreshFeedItemsInView();
   }

   public void onEventMainThread(FeedEntityChangedEvent event) {
      Queryable.from(feedItems).forEachR(item -> {
         if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
            FeedEntity feedEntity = event.getFeedEntity();
            if (feedEntity.getOwner() == null) {
               feedEntity.setOwner(item.getItem().getOwner());
            }
            item.setItem(feedEntity);
         }
      });

      refreshFeedItemsInView();
   }

   public void onEvent(FeedEntityCommentedEvent event) {
      Queryable.from(feedItems).forEachR(item -> {
         if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
            item.setItem(event.getFeedEntity());
         }
      });

      refreshFeedItemsInView();
   }

   public void onEvent(LikesPressedEvent event) {
      if (view.isVisibleOnScreen()) {
         feedInteractor.changeFeedEntityLikedStatusPipe()
               .send(new ChangeFeedEntityLikedStatusCommand(event.getModel()));
      }
   }

   private void subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(command -> itemLiked(command.getResult()))
                  .onFail(this::handleError));
   }

   public void onEvent(DeletePostEvent event) {
      if (!view.isVisibleOnScreen()) return;
      postsInteractor.deletePostPipe()
            .createObservable(new DeletePostCommand(event.getEntity().getUid()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DeletePostCommand>()
                  .onSuccess(deletePostCommand -> itemDeleted(event.getEntity()))
                  .onFail(this::handleError));
   }

   public void onEvent(TranslatePostEvent event) {
      if (view.isVisibleOnScreen()) {
         textualPostTranslationDelegate.translate(event.getPostFeedItem(), LocaleHelper.getDefaultLocaleFormatted());
      }
   }

   public void onEvent(DeletePhotoEvent event) {
      if (view.isVisibleOnScreen()) {
         tripImagesInteractor.deletePhotoPipe()
               .createObservable(new DeletePhotoCommand(event.getEntity().getUid()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<DeletePhotoCommand>()
                     .onSuccess(deletePhotoCommand -> itemDeleted(event.getEntity()))
                     .onFail(this::handleError));
      }
   }

   public void onEvent(LoadFlagEvent event) {
      if (view.isVisibleOnScreen()) flagDelegate.loadFlags(event.getFlaggableView(), this::handleError);
   }

   public void onEvent(ItemFlaggedEvent event) {
      if (view.isVisibleOnScreen()) flagDelegate.flagItem(new FlagData(event.getEntity()
            .getUid(), event.getFlagReasonId(), event.getNameOfReason()), view, this::handleError);
   }

   private void itemLiked(FeedEntity feedEntity) {
      Queryable.from(feedItems).forEachR(feedItem -> {
         FeedEntity item = feedItem.getItem();
         if (item.getUid().equals(feedEntity.getUid())) {
            item.syncLikeState(feedEntity);
         }
      });

      refreshFeedItemsInView();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Photo suggestions
   ///////////////////////////////////////////////////////////////////////////

   private void subscribePhotoGalleryCheck() {
      suggestedPhotoInteractor.getSuggestedPhotoCommandActionPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<SuggestedPhotoCommand>().onSuccess(suggestedPhotoCommand -> {
               if (hasNewPhotos(suggestedPhotoCommand.getResult())) {
                  suggestedPhotos = new ArrayList<>(Queryable.from(suggestedPhotoCommand.getResult())
                        .take(SUGGESTION_ITEM_CHUNK).toList());
               }
               refreshFeedItemsInView();
            }).onFail((suggestedPhotoCommand, throwable) -> refreshFeedItemsInView()));
   }

   public boolean hasNewPhotos(List<PhotoGalleryModel> photos) {
      return photos != null && !photos.isEmpty() && photos.get(0).getDateTaken() > db.getLastSuggestedPhotosSyncTime();
   }

   public void removeSuggestedPhotos() {
      suggestedPhotos.clear();
      suggestedPhotoHelper.reset();
      refreshFeedItemsInView();
   }

   public void takeSuggestionView(SuggestedPhotoCellPresenterHelper.View view, SuggestedPhotoCellPresenterHelper.OutViewBinder binder, Bundle bundle, Observable<Void> notificationObservable) {
      suggestedPhotoHelper = new SuggestedPhotoCellPresenterHelper();
      injectorProvider.get().inject(suggestedPhotoHelper);

      suggestedPhotoHelper.takeView(view, binder, bundle);
      suggestedPhotoHelper.subscribeNewPhotoNotifications(notificationObservable);
   }

   public void preloadSuggestionChunk(@NonNull PhotoGalleryModel model) {
      suggestedPhotoHelper.preloadSuggestionPhotos(model);
   }

   public void syncSuggestionViewState() {
      suggestedPhotoHelper.sync();
   }

   public void selectPhoto(@NonNull PhotoGalleryModel model) {
      suggestedPhotoHelper.selectPhoto(model);
   }

   public void attachSelectedSuggestionPhotos() {
      Observable.from(getSelectedSuggestionPhotos())
            .map(element -> {
               Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, element.getAbsolutePath());
               return new PhotoGalleryModel(pair.first, pair.second);
            })
            .map(photoGalleryModel -> {
               ArrayList<PhotoGalleryModel> chosenImages = new ArrayList<>();
               chosenImages.add(photoGalleryModel);
               return new MediaAttachment(chosenImages, MediaAttachment.Source.GALLERY);
            })
            .compose(new IoToMainComposer<>())
            .subscribe(mediaAttachment -> mediaPickerEventDelegate.post(mediaAttachment), error -> Timber.e(error, ""));
   }

   public List<PhotoGalleryModel> getSelectedSuggestionPhotos() {
      return suggestedPhotoHelper.selectedPhotos();
   }

   public long lastSyncTimestamp() {
      return suggestedPhotoHelper.lastSyncTime();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Unread conversations
   ///////////////////////////////////////////////////////////////////////////

   private void subscribeUnreadConversationsCount() {
      unreadConversationObservable.getObservable().compose(bindViewToMainComposer()).subscribe(count -> {
         unreadConversationCount = count;
         view.setUnreadConversationCount(count);
      }, throwable -> Timber.w("Can't get unread conversation count"));
   }

   private void subscribeFriendsNotificationsCount() {
      notificationCountEventDelegate.getObservable().compose(bindViewToMainComposer()).subscribe(event -> {
         view.setRequestsCount(getFriendsRequestsCount());
      }, throwable -> Timber.w("Can't get friends notifications count"));
   }

   private void refreshFeedItemsInView() {
      view.refreshFeedItems(feedItems, new UploadingPostsList(postUploads), suggestedPhotos);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Uploading handling
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onUploadResume(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadResume(compoundOperationModel);
   }

   @Override
   public void onUploadPaused(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadPaused(compoundOperationModel);
   }

   @Override
   public void onUploadRetry(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadRetry(compoundOperationModel);
   }

   @Override
   public void onUploadCancel(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadCancel(compoundOperationModel);
   }

   public interface View extends RxView, FlagDelegate.View, TextualPostTranslationDelegate.View, ApiErrorView, BlockingProgressView {

      void setRequestsCount(int count);

      void showFilter(List<Circle> circles, Circle selectedCircle);

      void setUnreadConversationCount(int count);

      void refreshFeedItems(List<FeedItem> feedItems, UploadingPostsList uploadingPostsList, List<PhotoGalleryModel> suggestedPhotos);

      void startLoading();

      void finishLoading();

      void showEdit(BucketBundle bucketBundle);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);
   }
}
