package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedActionHandlerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEditEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.utils.FeedUtils;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public abstract class ProfilePresenter<T extends ProfilePresenter.View, U extends User> extends Presenter<T>
   implements FeedActionHandlerPresenter, FeedEditEntityPresenter {

   protected U user;

   @State ArrayList<FeedItem> feedItems;

   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;

   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject TranslationDelegate translationDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;

   public ProfilePresenter() {
   }

   public ProfilePresenter(U user) {
      this.user = user;
   }

   @Override
   public void restoreInstanceState(Bundle savedState) {
      super.restoreInstanceState(savedState);
      if (savedState == null) feedItems = new ArrayList<>();
   }

   @Override
   public void onResume() {
      super.onResume();
      refreshFeed();
   }

   @Override
   public void takeView(T view) {
      super.takeView(view);
      if (feedItems.size() != 0) {
         view.refreshFeedItems(feedItems);
      }
      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      attachUserToView(user);
      loadProfile();
      subscribeToLikesChanges();
      translationDelegate.onTakeView(view, feedItems);
   }

   @Override
   public void dropView() {
      translationDelegate.onDropView();
      super.dropView();
   }

   protected void onProfileLoaded(U user) {
      attachUserToView(user);
      view.finishLoading();
   }

   private void attachUserToView(U user) {
      this.user = user;
      view.setUser(this.user);
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
   public void onFlagItem(FeedItem feedItem, int flagReasonId, String reason) {
      feedActionHandlerDelegate.onFlagItem(feedItem.getItem().getUid(), flagReasonId, reason, view, this::handleError);
   }

   public void onEventMainThread(FeedEntityChangedEvent event) {
      updateFeedEntity(event.getFeedEntity());
   }

   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      FeedUtils.updateFeedItemInList(feedItems, updatedFeedEntity);
      refreshFeedItems();
   }

   public void onEvent(FeedEntityCommentedEvent event) {
      Queryable.from(feedItems).forEachR(item -> {
         if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
            item.setItem(event.getFeedEntity());
         }
      });

      refreshFeedItems();
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
   public void onTranslateFeedEntity(FeedEntity translatableItem) {
      translationDelegate.translate(translatableItem, LocaleHelper.getDefaultLocaleFormatted());
   }

   @Override
   public void onShowOriginal(FeedEntity translatableItem) {
      translationDelegate.showOriginal(translatableItem);
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

   public void onRefresh() {
      view.startLoading();
      refreshFeed();
      loadProfile();
   }

   public boolean onLoadNext() {
      if (feedItems.isEmpty()) return false;
      loadNext(feedItems.get(feedItems.size() - 1).getCreatedAt());
      return true;
   }

   public abstract void refreshFeed();

   public abstract void loadNext(Date date);

   protected void refreshFeedSucceed(List<FeedItem> freshItems) {
      boolean noMoreElements = freshItems == null || freshItems.size() == 0;
      view.updateLoadingStatus(false, noMoreElements);
      view.finishLoading();
   }

   protected void addFeedItems(List<FeedItem> olderItems) {
      // server signals about end of pagination with empty page, NOT with items < page size
      boolean noMoreElements = olderItems == null || olderItems.size() == 0;
      view.updateLoadingStatus(false, noMoreElements);
   }

   protected void refreshFeedError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
      view.finishLoading();
   }

   protected void loadMoreItemsError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, true);
      view.finishLoading();
   }

   protected void onItemsChanged(List<FeedItem> newFeedItems) {
      feedItems.clear();
      feedItems.addAll(newFeedItems);
      refreshFeedItems();
   }

   public void refreshFeedItems() {
      view.refreshFeedItems(feedItems);
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

   public interface View extends RxView, FlagDelegate.View, TranslationDelegate.View, FeedEntityEditingView {
      void openPost();

      void openFriends();

      void openComments(FeedItem feedItem);

      void openTripImages(Route route, TripsImagesBundle tripImagesBundle);

      void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle);

      void notifyUserChanged();

      void setUser(User user);

      void startLoading();

      void finishLoading();

      void refreshFeedItems(List<FeedItem> items);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);
   }
}
