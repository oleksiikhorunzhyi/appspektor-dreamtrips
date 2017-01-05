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
import com.worldventures.dreamtrips.modules.feed.event.TranslatePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedActionHandlerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEditEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemsHolder;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntitiesHolderDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.modules.feed.view.util.TextualPostTranslationDelegate;
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
   implements FeedActionHandlerPresenter, FeedEditEntityPresenter, FeedItemsHolder {

   protected U user;

   @State ArrayList<FeedItem> feedItems;

   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;

   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject TextualPostTranslationDelegate textualPostTranslationDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject FeedEntitiesHolderDelegate feedEntitiesHolderDelegate;

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
      feedEntitiesHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
      attachUserToView(user);
      loadProfile();
      subscribeToLikesChanges();
      textualPostTranslationDelegate.onTakeView(view, feedItems);
   }

   @Override
   public void dropView() {
      textualPostTranslationDelegate.onDropView();
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

   @Override
   public void deleteFeedEntity(String uid) {
      feedEntitiesHolderDelegate.deleteFeedItemInList(feedItems, uid);
      refreshFeedItems();
   }

   @Override
   public void addFeedItem(FeedItem feedItem) {
      feedItems.add(0, feedItem);
      refreshFeedItems();
   }

   public void onEventMainThread(FeedEntityChangedEvent event) {
      updateFeedEntity(event.getFeedEntity());
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      feedEntitiesHolderDelegate.updateFeedItemInList(feedItems, updatedFeedEntity);
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

   public void onEvent(TranslatePostEvent event) {
      if (view.isVisibleOnScreen()) {
         textualPostTranslationDelegate.translate(event.getPostFeedItem(), LocaleHelper.getDefaultLocaleFormatted());
      }
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

   public void onLoadNext() {
      loadNext(feedItems.get(feedItems.size() - 1).getCreatedAt());
   }

   public abstract void refreshFeed();

   public abstract void loadNext(Date date);

   protected void refreshFeedSucceed(List<FeedItem> freshItems) {
      boolean noMoreElements = freshItems == null || freshItems.size() == 0;
      view.updateLoadingStatus(false, noMoreElements);
      //
      view.finishLoading();
      feedItems.clear();
      feedItems.addAll(freshItems);
      refreshFeedItems();
   }

   protected void addFeedItems(List<FeedItem> olderItems) {
      boolean noMoreElements = olderItems == null || olderItems.size() == 0;
      view.updateLoadingStatus(false, noMoreElements);
      //
      feedItems.addAll(olderItems);
      refreshFeedItems();
   }

   protected void refreshFeedError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
      view.finishLoading();
      refreshFeedItems();
   }

   protected void loadMoreItemsError(CommandWithError action, Throwable throwable) {
      handleError(action, throwable);
      view.updateLoadingStatus(false, false);
      addFeedItems(new ArrayList<>());
   }

   @Override
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

   public interface View extends RxView, FlagDelegate.View, TextualPostTranslationDelegate.View, FeedEntityEditingView {
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
