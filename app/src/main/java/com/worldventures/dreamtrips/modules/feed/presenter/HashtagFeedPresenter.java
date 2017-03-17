package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.storage.command.HashtagFeedStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.delegate.HashtagFeedStorageDelegate;
import com.worldventures.dreamtrips.modules.feed.utils.FeedUtils;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;

public class HashtagFeedPresenter<T extends HashtagFeedPresenter.View> extends JobPresenter<T>
   implements FeedActionHandlerPresenter, FeedEditEntityPresenter {

   private final static int FEEDS_PER_PAGE = 10;
   private final static int MIN_QUERY_LENGTH = 3;

   @State String query;
   @State ArrayList<FeedItem> feedItems = new ArrayList<>();
   @State ArrayList<HashtagSuggestion> hashtagSuggestions = new ArrayList<>();
   private Subscription storageSubscription;

   @Inject HashtagInteractor interactor;
   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;

   @Inject TranslationDelegate translationDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject HashtagFeedStorageDelegate hashtagFeedStorageDelegate;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      if (feedItems.size() != 0) {
         refreshFeedItems();
      }
      view.onSuggestionsReceived(query, hashtagSuggestions);

      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      subscribeToStorage();
      subscribeRefreshFeeds();
      subscribeLoadNextFeeds();
      subscribeSuggestions();
      subscribeToLikesChanges();
      translationDelegate.onTakeView(view, feedItems);
   }

   @Override
   public void dropView() {
      translationDelegate.onDropView();
      super.dropView();
   }

   @Nullable
   public String getQuery() {
      return query;
   }

   public void setQuery(String query) {
      this.query = query;
   }

   public void searchSuggestions(String fullText, String world) {
      if (world.replaceAll("#", "").length() >= MIN_QUERY_LENGTH) {
         view.showSuggestionProgress();
         interactor.getSuggestionPipe().send(new HashtagSuggestionCommand(fullText, world));
      } else {
         view.clearSuggestions();
         view.hideSuggestionProgress();
      }
   }

   private void subscribeToStorage() {
      if (storageSubscription != null && !storageSubscription.isUnsubscribed()) {
         storageSubscription.unsubscribe();
      }
      hashtagFeedStorageDelegate.setHashtag(query);
      storageSubscription = hashtagFeedStorageDelegate.startUpdatingStorage()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<HashtagFeedStorageCommand>()
                  .onSuccess(feedStorageCommand -> refreshFeed(feedStorageCommand.getResult()))
                  .onFail(this::handleError));
   }

   public void onRefresh() {
      if (!TextUtils.isEmpty(query)) {
         interactor.getRefreshFeedsByHashtagsPipe().send(new FeedByHashtagCommand.Refresh(query, FEEDS_PER_PAGE));
         subscribeToStorage();
      } else view.finishLoading();
   }

   public boolean loadNext() {
      if (feedItems.isEmpty() || TextUtils.isEmpty(query)) return false;
      interactor.getLoadNextFeedsByHashtagsPipe()
            .send(new FeedByHashtagCommand.LoadNext(query, FEEDS_PER_PAGE, feedItems.get(feedItems.size() - 1)
                  .getCreatedAt()));
      return true;
   }

   private void refreshFeed(List<FeedItem> newFeedItems) {
      feedItems.clear();
      feedItems.addAll(newFeedItems);
      refreshFeedItems();
   }

   private void subscribeRefreshFeeds() {
      interactor.getRefreshFeedsByHashtagsPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<FeedByHashtagCommand.Refresh>()
                  .onStart(refresh -> {
                     view.startLoading();
                     hashtagSuggestions.clear();
                  })
                  .onSuccess(refresh -> refreshFeedSucceed(refresh.getResult()))
                  .onFail((refresh, throwable) -> {
                     handleError(refresh, throwable);
                     refreshFeedError();
                  }));
   }

   private void refreshFeedSucceed(List<FeedItem> freshItems) {
      boolean noMoreFeeds = freshItems == null || freshItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
      view.finishLoading();
   }

   private void refreshFeedError() {
      view.updateLoadingStatus(false, false);
      view.finishLoading();
   }

   private void subscribeLoadNextFeeds() {
      interactor.getLoadNextFeedsByHashtagsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<FeedByHashtagCommand.LoadNext>()
                  .onSuccess(loadNext -> addFeedItems(loadNext.getResult()))
                  .onFail((loadNext, throwable) -> {
                     handleError(loadNext, throwable);
                     loadMoreItemsError();
                  }));
   }

   private void addFeedItems(List<FeedItem> newItems) {
      // server signals about end of pagination with empty page, NOT with items < page size
      boolean noMoreFeeds = newItems == null || newItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
   }

   private void loadMoreItemsError() {
      view.updateLoadingStatus(false, true);
   }

   private void subscribeSuggestions() {
      view.bindUntilStop(interactor.getSuggestionPipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<HashtagSuggestionCommand>().onSuccess(hashtagSuggestionCommand -> {
               hashtagSuggestions.clear();
               hashtagSuggestions.addAll(hashtagSuggestionCommand.getResult());
               view.onSuggestionsReceived(hashtagSuggestionCommand.getFullQueryText(), hashtagSuggestions);
               view.hideSuggestionProgress();
            }).onFail((hashtagSuggestionCommand, throwable) -> {
               handleError(hashtagSuggestionCommand, throwable);
               view.hideSuggestionProgress();
            }));
   }

   @Override
   public void onDownloadImage(String url) {
      feedActionHandlerDelegate.onDownloadImage(url, bindViewToMainComposer(), this::handleError);
   }

   public void refreshFeedItems() {
      view.refreshFeedItems(feedItems);
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
                  .onSuccess(this::itemLiked)
                  .onFail(this::handleError));
   }

   @Override
   public void onCommentItem(FeedItem feedItem) {
      view.showComments(feedItem);
   }

   @Override
   public void onTranslateFeedEntity(FeedEntity translatableItem) {
      translationDelegate.translate(translatableItem, LocaleHelper.getDefaultLocaleFormatted());
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

   private void itemLiked(ChangeFeedEntityLikedStatusCommand command) {
      Queryable.from(feedItems).forEachR(feedItem -> {
         FeedEntity item = feedItem.getItem();
         if (item.getUid().equals(command.getResult().getUid())) {
            item.syncLikeState(command.getResult());
         }
      });

      refreshFeedItems();
   }

   public void cancelLastSuggestionRequest() {
      interactor.getSuggestionPipe().cancelLatest();
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

   public interface View extends RxView, FlagDelegate.View, TranslationDelegate.View, ApiErrorView,
         FeedEntityEditingView {

      void startLoading();

      void finishLoading();

      void refreshFeedItems(List<FeedItem> events);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);

      void onSuggestionsReceived(String fullQueryText, @NonNull List<HashtagSuggestion> suggestionList);

      void clearSuggestions();

      void showSuggestionProgress();

      void hideSuggestionProgress();

      void showComments(FeedItem feedItem);
   }
}
