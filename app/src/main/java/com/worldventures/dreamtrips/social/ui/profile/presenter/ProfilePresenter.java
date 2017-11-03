package com.worldventures.dreamtrips.social.ui.profile.presenter;

import android.os.Bundle;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.Feature;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedActionHandlerPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEditEntityPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.social.ui.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagDelegate;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public abstract class ProfilePresenter<T extends ProfilePresenter.View> extends Presenter<T>
      implements FeedActionHandlerPresenter, FeedEditEntityPresenter {

   @State protected User user;
   @State ArrayList<FeedItem> feedItems;

   @Inject FeedInteractor feedInteractor;
   @Inject TranslationDelegate translationDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;

   public ProfilePresenter() {
      //do nothing
   }

   public ProfilePresenter(User user) {
      this.user = user;
   }

   @Override
   public void restoreInstanceState(Bundle savedState) {
      super.restoreInstanceState(savedState);
      if (savedState == null || feedItems == null) {
         feedItems = new ArrayList<>();
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      refreshFeed();
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      restoreItemsInView();
      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      attachUserToView(user);
      loadProfile();
      translationDelegate.onTakeView(view, feedItems, bindView());
   }

   void restoreItemsInView() {
      if (!feedItems.isEmpty()) {
         refreshFeedItems();
      }
   }

   @Override
   public void dropView() {
      translationDelegate.onDropView();
      super.dropView();
   }

   protected void onProfileLoaded(User user) {
      attachUserToView(user);
      view.finishLoading();
   }

   private void attachUserToView(User user) {
      this.user = user;
      refreshFeedItems();
   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      view.finishLoading();
   }

   public void makePost() {
      view.openPost();
   }

   protected abstract void loadProfile();

   public abstract void openBucketList();

   public abstract void openTripImages();

   public void openFriends() {
      if (featureManager.available(Feature.SOCIAL)) {
         view.openFriends();
      }
   }

   public User getUser() {
      return user;
   }

   @Override
   public void onDownloadImage(String url) {
      feedActionHandlerDelegate.onDownloadImage(url, bindViewToMainComposer(), this::handleError);
   }

   @Override
   public void onLoadFlags(Flaggable flaggableView) {
      feedActionHandlerDelegate.onLoadFlags(flaggableView, this::handleError);
   }

   @Override
   public void onFlagItem(String uid, int flagReasonId, String reason) {
      feedActionHandlerDelegate.onFlagItem(uid, flagReasonId, reason, view, this::handleError);
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
   public void onTranslateFeedEntity(FeedEntity translatableItem) {
      translationDelegate.translate(translatableItem);
   }

   @Override
   public void onShowOriginal(FeedEntity translatableItem) {
      translationDelegate.showOriginal(translatableItem);
   }

   public void onRefresh() {
      view.startLoading();
      refreshFeed();
      loadProfile();
   }

   public boolean onLoadNext() {
      if (feedItems.isEmpty()) {
         return false;
      }
      loadNext(feedItems.get(feedItems.size() - 1).getCreatedAt());
      return true;
   }

   public abstract void refreshFeed();

   public abstract void loadNext(Date date);

   void refreshFeedSucceed(List<FeedItem> freshItems) {
      boolean noMoreElements = freshItems == null || freshItems.size() == 0;
      view.updateLoadingStatus(false, noMoreElements);
      view.finishLoading();
   }

   void addFeedItems(List<FeedItem> olderItems) {
      // server signals about end of pagination with empty page, NOT with items < page size
      boolean noMoreElements = olderItems == null || olderItems.size() == 0;
      view.updateLoadingStatus(false, noMoreElements);
   }

   void refreshFeedError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
   }

   void loadMoreItemsError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, true);
   }

   void onItemsChanged(List<FeedItem> newFeedItems) {
      feedItems.clear();
      feedItems.addAll(newFeedItems);
      refreshFeedItems();
      view.dataSetChanged();
   }

   public void refreshFeedItems() {
      view.refreshFeedItems(feedItems, user);
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

   public interface View extends RxView, FlagDelegate.View, TranslationDelegate.View, FeedEntityEditingView {
      void openPost();

      void openFriends();

      void openComments(FeedItem feedItem);

      void openTripImages(TripImagesArgs tripImagesBundle);

      void openBucketList(Class<? extends BucketTabsFragment> clazz, ForeignBucketTabsBundle foreignBucketBundle);

      void startLoading();

      void finishLoading();

      void dataSetChanged();

      void refreshFeedItems(List<FeedItem> items, User user);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);

      void notifyDataSetChanged();
   }
}
