package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
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
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.FeedItemsVideoProcessingStatusInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.SuggestedPhotoInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.ViewFeedAction;
import com.worldventures.dreamtrips.modules.feed.service.command.BaseGetFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.delegate.FeedStorageDelegate;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
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

public class FeedPresenter extends Presenter<FeedPresenter.View> implements FeedActionHandlerPresenter,
      FeedEditEntityPresenter, UploadingListenerPresenter {

   private static final int SUGGESTION_ITEM_CHUNK = 15;

   @Inject SnappyRepository db;
   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject TranslationDelegate translationDelegate;
   @Inject DrawableUtil drawableUtil;
   @Inject UnreadConversationObservable unreadConversationObservable;
   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject FeedStorageDelegate feedStorageDelegate;

   @Inject FeedInteractor feedInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SuggestedPhotoInteractor suggestedPhotoInteractor;
   @Inject CirclesInteractor circlesInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject FeedItemsVideoProcessingStatusInteractor feedItemsVideoProcessingStatusInteractor;

   private Circle filterCircle;
   private SuggestedPhotoCellPresenterHelper suggestedPhotoHelper;

   private List<PostCompoundOperationModel> postUploads;
   private List<PhotoPickerModel> suggestedPhotos;
   @State ArrayList<FeedItem> feedItems;
   @State int unreadConversationCount;

   @Override
   public void restoreInstanceState(Bundle savedState) {
      super.restoreInstanceState(savedState);
      if (savedState == null || feedItems == null) feedItems = new ArrayList<>();
      filterCircle = db.getFilterCircle();
      if (filterCircle == null) filterCircle = createDefaultFilterCircle();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      updateCircles();
      subscribeToStorage();
      subscribeRefreshFeeds();
      subscribeLoadNextFeeds();
      subscribePhotoGalleryCheck();
      subscribeUnreadConversationsCount();
      subscribeFriendsNotificationsCount();
      subscribeToLikesChanges();
      subscribeToBackgroundUploadingOperations();
      translationDelegate.onTakeView(view, feedItems);

      if (feedItems.size() != 0) {
         refreshFeedItems();
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe().send(new ViewFeedAction());
      refreshFeed();
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
      translationDelegate.onDropView();
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

   private void subscribeToStorage() {
      feedStorageDelegate.startUpdatingStorage()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<FeedStorageCommand>()
                  .onSuccess(feedStorageCommand -> {
                     List<FeedItem> items = feedStorageCommand.getResult();
                     refreshFeed(items);
                     feedItemsVideoProcessingStatusInteractor.videosProcessingPipe()
                           .send(new FeedItemsVideoProcessingStatusCommand(items));
                  })
                  .onFail(this::handleError));
   }

   private void refreshFeed(List<FeedItem> newFeedItems) {
      feedItems.clear();
      feedItems.addAll(newFeedItems);
      refreshFeedItems();
   }

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
      view.finishLoading();
      suggestedPhotoInteractor.getSuggestedPhotoCommandActionPipe().send(new SuggestedPhotoCommand());
   }

   private void refreshFeedError(BaseGetFeedCommand action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
      view.finishLoading();
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
      // server signals about end of pagination with empty page, NOT with items < page size
      boolean noMoreFeeds = olderItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
   }

   private void loadMoreItemsError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, true);
   }

   public boolean loadNext() {
      if (feedItems.isEmpty()) return false;
      Date lastFeedDate = feedItems.get(feedItems.size() - 1).getCreatedAt();
      feedInteractor.getLoadNextAccountFeedPipe()
            .send(new GetAccountFeedCommand.LoadNext(filterCircle.getId(), lastFeedDate));
      return true;
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

   @Override
   public void onDownloadImage(String url) {
      feedActionHandlerDelegate.onDownloadImage(url, bindViewToMainComposer(), this::handleError);
   }

   private void subscribeToBackgroundUploadingOperations() {
      compoundOperationsInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess(compoundOperationsCommand -> {
                     postUploads = compoundOperationsCommand.getResult();
                     refreshFeedItems();
                  }));
   }

   @Override
   public void onLikeItem(FeedItem feedItem) {
      feedActionHandlerDelegate.onLikeItem(feedItem);
   }

   private void subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(command -> itemLiked(command.getResult()))
                  .onFail(this::handleError));
   }

   @Override
   public void onCommentItem(FeedItem feedItem) {
      view.openComments(feedItem);
   }

   @Override
   public void onTranslateFeedEntity(FeedEntity feedEntity) {
      translationDelegate.translate(feedEntity, LocaleHelper.getDefaultLocaleFormatted());
   }

   @Override
   public void onShowOriginal(FeedEntity translatableItem) {
      translationDelegate.showOriginal(translatableItem);
   }

   @Override
   public void onLoadFlags(Flaggable flaggableView) {
      feedActionHandlerDelegate.onLoadFlags(flaggableView, this::handleError);
   }

   @Override
   public void onFlagItem(FeedItem feedItem, int flagReasonId, String reason) {
      feedActionHandlerDelegate.onFlagItem(feedItem.getItem().getUid(), flagReasonId, reason, view, this::handleError);
   }

   private void itemLiked(FeedEntity feedEntity) {
      Queryable.from(feedItems).forEachR(feedItem -> {
         FeedEntity item = feedItem.getItem();
         if (item.getUid().equals(feedEntity.getUid())) {
            item.syncLikeState(feedEntity);
         }
      });

      refreshFeedItems();
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
               refreshFeedItems();
            }).onFail((suggestedPhotoCommand, throwable) -> refreshFeedItems()));
   }

   public boolean hasNewPhotos(List<PhotoPickerModel> photos) {
      return photos != null && !photos.isEmpty() && photos.get(0).getDateTaken() > db.getLastSuggestedPhotosSyncTime();
   }

   public void removeSuggestedPhotos() {
      suggestedPhotos.clear();
      suggestedPhotoHelper.reset();
      refreshFeedItems();
   }

   public void takeSuggestionView(SuggestedPhotoCellPresenterHelper.View view, SuggestedPhotoCellPresenterHelper.OutViewBinder binder, Bundle bundle, Observable<Void> notificationObservable) {
      suggestedPhotoHelper = new SuggestedPhotoCellPresenterHelper();
      injectorProvider.get().inject(suggestedPhotoHelper);

      suggestedPhotoHelper.takeView(view, binder, bundle);
      suggestedPhotoHelper.subscribeNewPhotoNotifications(notificationObservable);
   }

   public void preloadSuggestionChunk(@NonNull PhotoPickerModel model) {
      suggestedPhotoHelper.preloadSuggestionPhotos(model);
   }

   public void syncSuggestionViewState() {
      suggestedPhotoHelper.sync();
   }

   public void selectPhoto(@NonNull PhotoPickerModel model) {
      suggestedPhotoHelper.selectPhoto(model);
   }

   public void attachSelectedSuggestionPhotos() {
      Observable.from(getSelectedSuggestionPhotos())
            .map(element -> {
               Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, element.getAbsolutePath());
               return new PhotoPickerModel(pair.first, pair.second);
            })
            .map(photoGalleryModel -> {
               ArrayList<PhotoPickerModel> chosenImages = new ArrayList<>();
               chosenImages.add(photoGalleryModel);
               return new MediaAttachment(chosenImages, MediaAttachment.Source.GALLERY);
            })
            .compose(new IoToMainComposer<>())
            .subscribe(mediaAttachment -> mediaPickerEventDelegate.post(mediaAttachment), error -> Timber.e(error, ""));
   }

   public List<PhotoPickerModel> getSelectedSuggestionPhotos() {
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

   public void refreshFeedItems() {
      view.refreshFeedItems(feedItems, new UploadingPostsList(postUploads), suggestedPhotos);
   }

   @Override
   public void onEditTextualPost(TextualPost textualPost) {
      feedActionHandlerDelegate.onEditTextualPost(textualPost);
   }

   @Override
   public void onDeleteTextualPost(TextualPost textualPost) {
      feedActionHandlerDelegate.onDeleteTextualPost(textualPost);
   }

   @Override
   public void onEditPhoto(Photo photo) {
      feedActionHandlerDelegate.onEditPhoto(photo);
   }

   @Override
   public void onDeletePhoto(Photo photo) {
      feedActionHandlerDelegate.onDeletePhoto(photo);
   }

   @Override
   public void onEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type) {
      feedActionHandlerDelegate.onEditBucketItem(bucketItem, type);
   }

   @Override
   public void onDeleteBucketItem(BucketItem bucketItem) {
      feedActionHandlerDelegate.onDeleteBucketItem(bucketItem);
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

   public interface View extends RxView, FlagDelegate.View, TranslationDelegate.View, BlockingProgressView,
         FeedEntityEditingView {

      void setRequestsCount(int count);

      void showFilter(List<Circle> circles, Circle selectedCircle);

      void setUnreadConversationCount(int count);

      void refreshFeedItems(List<FeedItem> feedItems, UploadingPostsList uploadingPostsList, List<PhotoPickerModel> suggestedPhotos);

      void startLoading();

      void finishLoading();

      void openComments(FeedItem feedItem);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);
   }
}
