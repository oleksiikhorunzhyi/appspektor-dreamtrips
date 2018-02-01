package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.ui.activity.MessengerActivity;
import com.messenger.util.UnreadConversationObservable;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.Circle;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.ui.util.permission.PermissionConstants;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.command.NotificationCountChangedCommand;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.SuggestedPhotoInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.FriendsAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.ViewFeedAction;
import com.worldventures.dreamtrips.social.ui.feed.service.command.BaseGetFeedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.FeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.social.ui.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.social.ui.flags.model.FlagData;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagDelegate;
import com.worldventures.dreamtrips.social.service.friends.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;

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

   @Inject SocialSnappyRepository socialDb;
   @Inject TranslationDelegate translationDelegate;
   @Inject UnreadConversationObservable unreadConversationObservable;
   @Inject UserNotificationInteractor userNotificationInteractor;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject FeedStorageDelegate feedStorageDelegate;
   @Inject FeedInteractor feedInteractor;
   @Inject SuggestedPhotoInteractor suggestedPhotoInteractor;
   @Inject CirclesInteractor circlesInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PingAssetStatusInteractor assetStatusInteractor;
   @Inject SuggestedPhotoCellPresenterHelper suggestedPhotoHelper;
   @Inject PermissionDispatcher permissionDispatcher;

   Circle filterCircle;
   List<PostCompoundOperationModel> postUploads;
   boolean shouldShowSuggestionItems;
   @State ArrayList<FeedItem> feedItems;
   @State int unreadConversationCount;
   @State boolean permissionPreviouslyDenied = false;

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
      if (feedItems.size() != 0) {
         refreshFeedItems();
      }
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
      if (firstCreation || feedItems == null) {
         feedItems = new ArrayList<>();
      }
   }

   void restoreCircle() {
      filterCircle = socialDb.getFilterCircle();
      if (filterCircle == null) {
         filterCircle = createDefaultFilterCircle();
      }
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
      socialDb.saveFilterCircle(selectedCircle);
      refreshFeed();
   }

   @SuppressWarnings("unchecked")
   public void actionFilter() {
      circlesInteractor.getPipe()
            .createObservable(new GetCirclesCommand())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>()
                  .onStart(circlesCommand -> view.showBlockingProgress())
                  .onSuccess(circlesCommand ->
                        onCirclesSuccess((List<Circle>) circlesCommand.getResult())) // generic problem java vs kotlin
                  .onFail(this::onCirclesError));
   }

   void updateCircles() {
      circlesInteractor.getPipe().send(new GetCirclesCommand());
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

   void subscribeToStorage() {
      feedStorageDelegate.observeStorageCommand()
            .compose(bindViewToMainComposer())
            .map(Command::getResult)
            .subscribe(feedItems1 -> feedChanged(feedItems1), this::handleError);
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
      if (!permissionPreviouslyDenied) {
         suggestPhotoPermission();
      }
   }

   private void refreshFeedError(BaseGetFeedCommand action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false);
      view.finishLoading();
   }

   public void refreshFeed() {
      view.startLoading();
      feedInteractor.getRefreshAccountFeedPipe().send(new GetAccountFeedCommand.Refresh(filterCircle.getId()));
   }

   private void suggestPhotoPermission() {
      permissionDispatcher.requestPermission(PermissionConstants.READ_STORAGE_PERMISSION, false)
            .compose(bindView())
            .subscribe(new PermissionSubscriber()
                  .onPermissionDeniedAction(() -> {
                     permissionPreviouslyDenied = true;
                     refreshFeedItems();
                  })
                  .onPermissionGrantedAction(() ->
                        suggestedPhotoInteractor.getSuggestedPhotoCommandActionPipe().send(new SuggestedPhotoCommand())
                  ));
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
      if (feedItems.isEmpty()) {
         return false;
      }
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
      userNotificationInteractor.notificationCountChangedPipe().send(new NotificationCountChangedCommand());
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
      socialDb.saveLastSuggestedPhotosSyncTime(System.currentTimeMillis());
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
      userNotificationInteractor.notificationCountChangedPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> view.setRequestsCount(command.getFriendNotificationCount()));
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

   public void onFriendsOpened() {
      analyticsInteractor.analyticsActionPipe().send(FriendsAnalyticsAction.openFriends());
   }

   public interface View extends RxView, FlagDelegate.View, TranslationDelegate.View, BlockingProgressView,
         FeedEntityEditingView {

      void setRequestsCount(int count);

      void showFilter(List<Circle> circles, Circle selectedCircle);

      void setUnreadConversationCount(int count);

      void refreshFeedItems(@NonNull List<FeedItem> feedItems,
            @Nullable List<PostCompoundOperationModel> uploadingPostsList, boolean shouldShowSuggestions);

      void dataSetChanged();

      void startLoading();

      void finishLoading();

      void openComments(FeedItem feedItem);

      void updateLoadingStatus(boolean noMoreElements);

      void openCreatePostScreen(MediaPickerAttachment mediaPickerAttachment);
   }
}
