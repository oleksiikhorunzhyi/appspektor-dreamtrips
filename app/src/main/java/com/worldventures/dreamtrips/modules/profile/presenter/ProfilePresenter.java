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
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.DownloadPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.TranslatePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.view.util.TextualPostTranslationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public abstract class ProfilePresenter<T extends ProfilePresenter.View, U extends User> extends Presenter<T> {

   protected U user;

   @State ArrayList<FeedItem> feedItems;

   @Inject LocaleHelper localeHelper;
   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;
   @Inject TextualPostTranslationDelegate textualPostTranslationDelegate;
   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;

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
      //
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

      view.refreshFeedItems(feedItems);
   }

   public void onEvent(FeedEntityDeletedEvent event) {
      itemDeleted(event.getEventModel());
   }

   public void onEvent(FeedItemAddedEvent event) {
      feedItems.add(0, event.getFeedItem());
      view.refreshFeedItems(feedItems);
   }

   public void onEvent(FeedEntityChangedEvent event) {
      Queryable.from(feedItems).forEachR(item -> {
         if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
            FeedEntity feedEntity = event.getFeedEntity();
            if (feedEntity.getOwner() == null) {
               feedEntity.setOwner(item.getItem().getOwner());
            }
            item.setItem(feedEntity);
         }
      });

      view.refreshFeedItems(feedItems);
   }

   public void onEvent(FeedEntityCommentedEvent event) {
      Queryable.from(feedItems).forEachR(item -> {
         if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
            item.setItem(event.getFeedEntity());
         }
      });

      view.refreshFeedItems(feedItems);
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
         textualPostTranslationDelegate.translate(event.getPostFeedItem(), localeHelper.getDefaultLocaleFormatted());
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

   private void itemLiked(FeedEntity feedEntity) {
      Queryable.from(feedItems).forEachR(feedItem -> {
         FeedEntity item = feedItem.getItem();
         if (item.getUid().equals(feedEntity.getUid())) {
            item.syncLikeState(feedEntity);
         }
      });

      view.refreshFeedItems(feedItems);
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
      view.refreshFeedItems(feedItems);
   }

   protected void addFeedItems(List<FeedItem> olderItems) {
      boolean noMoreElements = olderItems == null || olderItems.size() == 0;
      view.updateLoadingStatus(false, noMoreElements);
      //
      feedItems.addAll(olderItems);
      view.refreshFeedItems(feedItems);
   }

   protected void refreshFeedError(CommandWithError action, Throwable throwable) {
      view.informUser(action.getErrorMessage());
      view.updateLoadingStatus(false, false);
      view.finishLoading();
      view.refreshFeedItems(feedItems);
   }

   protected void loadMoreItemsError(CommandWithError action, Throwable throwable) {
      view.informUser(action.getErrorMessage());
      view.updateLoadingStatus(false, false);
      addFeedItems(new ArrayList<>());
   }

   public interface View extends RxView, TextualPostTranslationDelegate.View {

      void openPost();

      void openFriends();

      void openTripImages(Route route, TripsImagesBundle tripImagesBundle);

      void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle);

      void notifyUserChanged();

      void setUser(User user);

      void startLoading();

      void finishLoading();

      void refreshFeedItems(List<FeedItem> events);

      void showEdit(BucketBundle bucketBundle);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);
   }
}
