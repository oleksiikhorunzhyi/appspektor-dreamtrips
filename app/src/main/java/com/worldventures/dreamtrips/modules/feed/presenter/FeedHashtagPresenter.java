package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
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
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.event.TranslatePostEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.view.util.TextualPostTranslationDelegate;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FeedHashtagPresenter<T extends FeedHashtagPresenter.View> extends JobPresenter<T> {

   private final static int FEEDS_PER_PAGE = 10;
   private final static int MIN_QUERY_LENGTH = 3;

   @State String query;
   @State ArrayList<FeedItem> feedItems = new ArrayList<>();
   @State ArrayList<HashtagSuggestion> hashtagSuggestions = new ArrayList<>();

   @Inject LocaleHelper localeHelper;
   @Inject TextualPostTranslationDelegate textualPostTranslationDelegate;
   @Inject HashtagInteractor interactor;
   @Inject FeedEntityManager entityManager;
   @Inject BucketInteractor bucketInteractor;
   @Inject FlagsInteractor flagsInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;

   private FlagDelegate flagDelegate;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      if (feedItems.size() != 0) {
         view.refreshFeedItems(feedItems);
      }
      view.onSuggestionsReceived(query, hashtagSuggestions);

      subscribeRefreshFeeds();
      subscribeLoadNextFeeds();
      subscribeSuggestions();
      textualPostTranslationDelegate.onTakeView(view, feedItems);
   }

   @Override
   public void dropView() {
      textualPostTranslationDelegate.onDropView();
      super.dropView();
   }

   @Override
   public void onInjected() {
      super.onInjected();
      entityManager.setFeedEntityManagerListener(this);
      flagDelegate = new FlagDelegate(flagsInteractor);
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
         hashtagSuggestions.clear();
         view.startLoading();
         interactor.getRefreshFeedsByHashtagsPipe().send(new FeedByHashtagCommand.Refresh(query, FEEDS_PER_PAGE));
      }
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
               .onSuccess(refresh -> refreshFeedSucceed(refresh.getResult()))
               .onFail((refresh, throwable) -> {
                  view.informUser(refresh.getErrorMessage());
                  refreshFeedError();
            }));
   }

   private void refreshFeedSucceed(List<FeedItem> freshItems) {
      boolean noMoreFeeds = freshItems == null || freshItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
      //
      view.finishLoading();
      feedItems.clear();
      feedItems.addAll(freshItems);
      //
      view.refreshFeedItems(feedItems);
   }

   private void refreshFeedError() {
      view.updateLoadingStatus(false, false);
      view.finishLoading();
      view.refreshFeedItems(feedItems);
   }

   private void subscribeLoadNextFeeds() {
      interactor.getLoadNextFeedsByHashtagsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<FeedByHashtagCommand.LoadNext>()
                  .onSuccess(loadNext -> addFeedItems(loadNext.getResult()))
                  .onFail((loadNext, throwable) -> {
                     view.informUser(loadNext.getErrorMessage());
                     loadMoreItemsError();
                  }));
   }

   private void addFeedItems(List<FeedItem> newItems) {
      boolean noMoreFeeds = newItems == null || newItems.size() == 0;
      view.updateLoadingStatus(false, noMoreFeeds);
      //
      feedItems.addAll(newItems);
      view.refreshFeedItems(feedItems);
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
               Timber.e(throwable, "");
               view.hideSuggestionProgress();
            }));
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
         FeedEntity model = event.getModel();
         if (model.isLiked()) {
            entityManager.unlike(model);
         } else {
            entityManager.like(model);
         }
      }
   }

   public void onEvent(EntityLikedEvent event) {
      itemLiked(event.getFeedEntity());
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

   public void onEvent(LoadFlagEvent event) {
      if (view.isVisibleOnScreen()) flagDelegate.loadFlags(event.getFlaggableView(), this::handleError);
   }

   public void onEvent(ItemFlaggedEvent event) {
      if (view.isVisibleOnScreen()) flagDelegate.flagItem(new FlagData(event.getEntity()
            .getUid(), event.getFlagReasonId(), event.getNameOfReason()), view, this::handleError);
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

   public void cancelLastSuggestionRequest() {
      interactor.getSuggestionPipe().cancelLatest();
   }

   public interface View extends RxView, FlagDelegate.View, TextualPostTranslationDelegate.View, ApiErrorView {

      void startLoading();

      void finishLoading();

      void refreshFeedItems(List<FeedItem> events);

      void updateLoadingStatus(boolean loading, boolean noMoreElements);

      void onSuggestionsReceived(String fullQueryText, @NonNull List<HashtagSuggestion> suggestionList);

      void clearSuggestions();

      void showEdit(BucketBundle bucketBundle);

      void showSuggestionProgress();

      void hideSuggestionProgress();
   }
}
