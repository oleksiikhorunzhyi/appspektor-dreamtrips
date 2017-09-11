package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.messenger.ui.activity.MessengerActivity;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.SuggestedPhotoInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.ViewFeedAction;
import com.worldventures.dreamtrips.modules.feed.service.command.BaseGetFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.storage.delegate.FeedStorageDelegate;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeleteVideoCommand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FeedPresenter extends Presenter<FeedPresenter.View> implements FeedActionHandlerPresenter,
      FeedEditEntityPresenter, UploadingListenerPresenter {

   @Inject SnappyRepository db;
   @Inject TranslationDelegate translationDelegate;
   @Inject UnreadConversationObservable unreadConversationObservable;
   @Inject NotificationCountEventDelegate notificationCountEventDelegate;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject FeedStorageDelegate feedStorageDelegate;
   @Inject FeedInteractor feedInteractor;
   @Inject SuggestedPhotoInteractor suggestedPhotoInteractor;
   @Inject CirclesInteractor circlesInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PingAssetStatusInteractor assetStatusInteractor;
   @Inject SuggestedPhotoCellPresenterHelper suggestedPhotoHelper;

   Circle filterCircle;
   List<PostCompoundOperationModel> postUploads;
   boolean shouldShowSuggestionItems;
   @State ArrayList<FeedItem> feedItems;
   @State int unreadConversationCount;

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      updateCircles();
      subscribeToStorage();
      subscribeRefreshFeeds();
      subscribeLoadNextFeeds();
      subscribePhotoGalleryCheck();
      subscribeUnreadConversationsCount();
      subscribeFriendsNotificationsCount();
      subscribeToBackgroundUploadingOperations();
      translationDelegate.onTakeView(view, feedItems, bindView());
      if (feedItems.size() != 0) refreshFeedItems();
      refreshFeed();
   }

   @Override
   public void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe().send(new ViewFeedAction());
   }

   @Override
   public void restoreInstanceState(Bundle savedState) {
      super.restoreInstanceState(savedState);
      restoreFeedItems(savedState == null);
      restoreCircle();
   }

   void restoreFeedItems(boolean firstCreation) {
      if (firstCreation || feedItems == null) feedItems = new ArrayList<>();
   }

   void restoreCircle() {
      filterCircle = db.getFilterCircle();
      if (filterCircle == null) filterCircle = createDefaultFilterCircle();
   }

   @Override
   public void saveInstanceState(Bundle outState) {
      super.saveInstanceState(outState);
      suggestedPhotoHelper.saveInstanceState(outState);
   }

   @Override
   public void dropView() {
      translationDelegate.onDropView();
      suggestedPhotoHelper.dropView();
      super.dropView();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Circles
   ///////////////////////////////////////////////////////////////////////////

   private Circle createDefaultFilterCircle() {
      return Circle.withTitle(context.getString(R.string.all));
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
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>()
                  .onStart(circlesCommand -> view.showBlockingProgress())
                  .onSuccess(circlesCommand -> onCirclesSuccess(circlesCommand.getResult()))
                  .onFail(this::onCirclesError));
   }

   void updateCircles() {
      circlesInteractor.pipe().send(new GetCirclesCommand());
   }

   private void onCirclesSuccess(List<Circle> resultCircles) {
      resultCircles.add(createDefaultFilterCircle());
      view.hideBlockingProgress();
      view.showFilter(resultCircles, filterCircle);
   }

   private void onCirclesError(CommandWithError commandWithError, Throwable throwable) {
      view.hideBlockingProgress();
      handleError(commandWithError, throwable);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Feed items
   ///////////////////////////////////////////////////////////////////////////

   public void refreshFeedItems() {
      view.refreshFeedItems(feedItems, postUploads, shouldShowSuggestionItems);
   }

   public void refreshFeed() {
      feedInteractor.getRefreshAccountFeedPipe().send(new GetAccountFeedCommand.Refresh(filterCircle.getId()));
   }

   void subscribeToStorage() {
      feedStorageDelegate.observeStorageCommand()
            .compose(bindViewToMainComposer())
            .map(Command::getResult)
            .subscribe(this::feedChanged, this::handleError);
   }

   private void feedChanged(List<FeedItem> items) {
      feedItems.clear();
      feedItems.addAll(items);
      refreshFeedItems();
      view.dataSetChanged();
      assetStatusInteractor.feedItemsVideoProcessingPipe().send(new FeedItemsVideoProcessingStatusCommand(items));
   }

   void subscribeRefreshFeeds() {
      feedInteractor.getRefreshAccountFeedPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAccountFeedCommand.Refresh>()
                  .onStart(action -> view.startLoading())
                  .onSuccess(action -> refreshFeedSucceed(action.getResult()))
                  .onFail(this::refreshFeedError));
   }

   private void refreshFeedSucceed(List<FeedItem> freshItems) {
      boolean noMoreFeeds = freshItems.size() == 0;
      view.updateLoadingStatus(noMoreFeeds);
      view.finishLoading();
      suggestedPhotoInteractor.getSuggestedPhotoCommandActionPipe().send(new SuggestedPhotoCommand());
   }

   private void refreshFeedError(BaseGetFeedCommand action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false);
      view.finishLoading();
   }

   void subscribeLoadNextFeeds() {
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
      view.updateLoadingStatus(noMoreFeeds);
   }

   private void loadMoreItemsError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(true);
   }

   public boolean loadNext() {
      if (feedItems.isEmpty()) return false;
      Date lastFeedDate = feedItems.get(feedItems.size() - 1).getCreatedAt();
      feedInteractor.getLoadNextAccountFeedPipe()
            .send(new GetAccountFeedCommand.LoadNext(filterCircle.getId(), lastFeedDate));
      return true;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Background uploading
   ///////////////////////////////////////////////////////////////////////////

   void subscribeToBackgroundUploadingOperations() {
      compoundOperationsInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess(compoundOperationsCommand -> {
                     postUploads = compoundOperationsCommand.getResult();
                     refreshFeedItems();
                  }));
   }

   ///////////////////////////////////////////////////////////////////////////
   // User interactions
   ///////////////////////////////////////////////////////////////////////////

   public void onUnreadConversationsClick() {
      MessengerActivity.startMessenger(activityRouter.getContext());
   }

   public void menuInflated() {
      view.setRequestsCount(db.getFriendsRequestsCount());
      view.setUnreadConversationCount(unreadConversationCount);
   }

   @Override
   public void onDownloadImage(String url) {
      feedActionHandlerDelegate.onDownloadImage(url, bindViewToMainComposer(), this::handleError);
   }

   @Override
   public void onLikeItem(FeedItem feedItem) {
      feedActionHandlerDelegate.onLikeItem(feedItem);
   }

   @Override
   public void onCommentItem(FeedItem feedItem) {
      view.openComments(feedItem);
   }

   @Override
   public void onTranslateFeedEntity(FeedEntity feedEntity) {
      translationDelegate.translate(feedEntity);
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
   public void onFlagItem(String uid, int flagReasonId, String reason) {
      feedActionHandlerDelegate.onFlagItem(new FlagData(uid, flagReasonId, reason), view, this::handleError);
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
   public void onDeleteVideo(Video video) {
      feedInteractor.deleteVideoPipe().send(new DeleteVideoCommand(video));
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
   // Suggestions
   ///////////////////////////////////////////////////////////////////////////

   public void takeSuggestionView(SuggestedPhotoCellPresenterHelper.View view, Bundle bundle, Observable<Void> notificationObservable) {
      suggestedPhotoHelper.takeView(view, bindView(), bundle);
      suggestedPhotoHelper.subscribeNewPhotoNotifications(notificationObservable);
   }

   void subscribePhotoGalleryCheck() {
      suggestedPhotoInteractor.getSuggestedPhotoCommandActionPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<SuggestedPhotoCommand>()
                  .onSuccess(suggestedPhotoCommand -> {
                     shouldShowSuggestionItems = suggestedPhotoCommand.getResult();
                     refreshFeedItems();
                  }).onFail((suggestedPhotoCommand, throwable) -> refreshFeedItems()));
   }


   public void removeSuggestedPhotos() {
      db.saveLastSuggestedPhotosSyncTime(System.currentTimeMillis());
      shouldShowSuggestionItems = false;
      suggestedPhotoHelper.reset();
      refreshFeedItems();
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

   public void attachSuggestionsClicked() {
      view.openCreatePostScreen(suggestedPhotoHelper.getSelectedAttachments());
      removeSuggestedPhotos();
   }

   public long lastSyncTimestamp() {
      return suggestedPhotoHelper.lastSyncTime();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Counters
   ///////////////////////////////////////////////////////////////////////////

   void subscribeUnreadConversationsCount() {
      unreadConversationObservable.getObservable().compose(bindViewToMainComposer()).subscribe(count -> {
         unreadConversationCount = count;
         view.setUnreadConversationCount(count);
      }, throwable -> Timber.w("Can't get unread conversation count"));
   }

   void subscribeFriendsNotificationsCount() {
      notificationCountEventDelegate.getObservable().compose(bindViewToMainComposer()).subscribe(event -> {
         view.setRequestsCount(db.getFriendsRequestsCount());
      }, throwable -> Timber.w("Can't get friends notifications count"));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Uploading callbacks
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

      void refreshFeedItems(List<FeedItem> feedItems, List<PostCompoundOperationModel> uploadingPostsList, boolean shouldShowSuggestions);

      void dataSetChanged();

      void startLoading();

      void finishLoading();

      void openComments(FeedItem feedItem);

      void updateLoadingStatus(boolean noMoreElements);

      void openCreatePostScreen(MediaPickerAttachment mediaPickerAttachment);
   }
}
