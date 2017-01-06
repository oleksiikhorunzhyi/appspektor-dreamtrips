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
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntitiesHolderDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
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

public class FeedHashtagPresenter<T extends FeedHashtagPresenter.View> extends JobPresenter<T>
   implements FeedActionHandlerPresenter, FeedEditEntityPresenter, FeedItemsHolder {

   private final static int FEEDS_PER_PAGE = 10;
   private final static int MIN_QUERY_LENGTH = 3;

   @State String query;
   @State ArrayList<FeedItem> feedItems = new ArrayList<>();
   @State ArrayList<HashtagSuggestion> hashtagSuggestions = new ArrayList<>();

   @Inject HashtagInteractor interactor;
   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;

   @Inject TranslationDelegate translationDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject FeedEntitiesHolderDelegate feedEntitiesHolderDelegate;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      if (feedItems.size() != 0) {
         refreshFeedItems();
      }
      view.onSuggestionsReceived(query, hashtagSuggestions);

      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      feedEntitiesHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
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

   public void onRefresh() {
      if (!TextUtils.isEmpty(query)) {
         interactor.getRefreshFeedsByHashtagsPipe().send(new FeedByHashtagCommand.Refresh(query, FEEDS_PER_PAGE));
      } else view.finishLoading();
   }

   public void loadNext() {
      if (feedItems.size() > 0) {
         if (!TextUtils.isEmpty(query)) {
            interactor.getLoadNextFeedsByHashtagsPipe()
                  .send(new FeedByHashtagCommand.LoadNext(query, FEEDS_PER_PAGE, feedItems.get(feedItems.size() - 1)
                        .getCreatedAt()));
         }
      }
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
      feedItems.clear();
      feedItems.addAll(freshItems);
      refreshFeedItems();
   }

   private void refreshFeedError() {
      view.updateLoadingStatus(false, false);
      view.finishLoading();
      refreshFeedItems();
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
      boolean noMoreFeeds = newItems == null || newItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
      //
      feedItems.addAll(newItems);
      refreshFeedItems();
   }

   private void loadMoreItemsError() {
      view.updateLoadingStatus(false, false);
      addFeedItems(new ArrayList<>());
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

   @Override
   public void deleteFeedEntity(String uid) {
      feedEntitiesHolderDelegate.deleteFeedItemInList(feedItems, uid);
      refreshFeedItems();
   }

   @Override
   public void refreshFeedItems() {
      view.refreshFeedItems(feedItems);
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
