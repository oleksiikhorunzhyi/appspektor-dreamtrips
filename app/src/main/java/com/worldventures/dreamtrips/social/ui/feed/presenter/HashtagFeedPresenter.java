package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityCopyHelper;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.HashtagFeedStorageCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.HashtagFeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.social.ui.feed.view.util.TranslationDelegate;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagDelegate;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;

public class HashtagFeedPresenter<T extends HashtagFeedPresenter.View> extends Presenter<T>
      implements FeedActionHandlerPresenter, FeedEditEntityPresenter {

   private final static int FEEDS_PER_PAGE = 10;
   private final static int MIN_QUERY_LENGTH = 3;

   @State String query;
   @State ArrayList<FeedItem> feedItems = new ArrayList<>();
   @State ArrayList<HashtagSuggestion> hashtagSuggestions = new ArrayList<>();
   @State boolean loading;
   private Subscription storageSubscription;

   @Inject HashtagInteractor interactor;

   @Inject TranslationDelegate translationDelegate;
   @Inject FeedInteractor feedInteractor;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject HashtagFeedStorageDelegate hashtagFeedStorageDelegate;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      if (feedItems.size() != 0) {
         refreshFeedItems();
      }
      view.onSuggestionsReceived(query, hashtagSuggestions);
      view.updateLoadingStatus(loading, false);

      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      subscribeToStorage();
      subscribeRefreshFeeds();
      subscribeLoadNextFeeds();
      subscribeSuggestions();
      translationDelegate.onTakeView(view, feedItems, bindView());
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
                  .onSuccess(feedStorageCommand -> feedUpdated(feedStorageCommand.getResult()))
                  .onFail(this::handleError));
   }

   public void onRefresh() {
      if (!TextUtils.isEmpty(query)) {
         interactor.getRefreshFeedsByHashtagsPipe().send(new FeedByHashtagCommand.Refresh(query, FEEDS_PER_PAGE));
         subscribeToStorage();
      } else {
         view.finishLoading();
      }
   }

   public boolean loadNext() {
      if (feedItems.isEmpty() || TextUtils.isEmpty(query)) {
         loading = false;
      }
      interactor.getLoadNextFeedsByHashtagsPipe()
            .send(new FeedByHashtagCommand.LoadNext(query, FEEDS_PER_PAGE, feedItems.get(feedItems.size() - 1)
                  .getCreatedAt()));
      view.showLoading();
      loading = true;
      return loading;
   }

   private void feedUpdated(List<FeedItem> newFeedItems) {
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
      loading = false;
      view.updateLoadingStatus(false, noMoreFeeds);
      view.finishLoading();
   }

   private void refreshFeedError() {
      loading = false;
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
      loading = false;
      view.updateLoadingStatus(false, noMoreFeeds);
   }

   private void loadMoreItemsError() {
      loading = false;
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
      view.refreshFeedItems(FeedEntityCopyHelper.copyFeedItems(feedItems));
   }

   @Override
   public void onLikeItem(FeedItem feedItem) {
      feedActionHandlerDelegate.onLikeItem(feedItem);
   }

   @Override
   public void onCommentItem(FeedItem feedItem) {
      view.showComments(feedItem);
   }

   @Override
   public void onTranslateFeedEntity(FeedEntity translatableItem) {
      translationDelegate.translate(translatableItem);
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
      feedActionHandlerDelegate.onFlagItem(uid, flagReasonId, reason, view, this::handleError);
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

      void startLoading();

      void finishLoading();

      void refreshFeedItems(List<FeedItem> events);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);

      void onSuggestionsReceived(String fullQueryText, @NonNull List<HashtagSuggestion> suggestionList);

      void clearSuggestions();

      void showSuggestionProgress();

      void hideSuggestionProgress();

      void showLoading();

      void showComments(FeedItem feedItem);
   }
}
