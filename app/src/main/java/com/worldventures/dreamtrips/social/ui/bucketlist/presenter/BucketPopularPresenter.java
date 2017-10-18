package com.worldventures.dreamtrips.social.ui.bucketlist.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.analytics.BucketItemAddedFromPopularAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.analytics.BucketPopularTabViewAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemSuggestionsCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemsCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl;

import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

import static com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.COMPLETED;
import static com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.NEW;

public class BucketPopularPresenter extends Presenter<BucketPopularPresenter.View> {

   public static final int SEARCH_THRESHOLD = 2;

   @Inject BucketInteractor bucketInteractor;

   private BucketItem.BucketType type;

   @State String query;

   public BucketPopularPresenter(BucketItem.BucketType type) {
      super();
      this.type = type;
   }

   @Override
   public void onResume() {
      super.onResume();
      if (view.getItemsCount() == 0) reload();
   }

   public void onSearch(String query) {
      this.query = query;
      if (query.length() > SEARCH_THRESHOLD) {
         searchPopularItems(query);
      }
   }

   void searchPopularItems(String query) {
      bucketInteractor.getPopularBucketItemSuggestionsPipe()
            .createObservable(new GetPopularBucketItemSuggestionsCommand(type, query))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPopularBucketItemSuggestionsCommand>()
                  .onStart(command -> view.startLoading())
                  .onFail(this::handleError)
                  .onSuccess(command -> onSearchSucceed(command.getResult())));
   }

   public void onSelected() {
      analyticsInteractor.analyticsActionPipe().send(new BucketPopularTabViewAnalyticsAction(type));
   }

   private void onSearchSucceed(List<PopularBucketItem> items) {
      if (view != null) {
         view.finishLoading();
         view.setFilteredItems(items);
      }
   }

   public void searchClosed() {
      view.flushFilter();
   }

   public void onAdd(PopularBucketItem popularBucketItem) {
      add(popularBucketItem, false);
   }

   public void onDone(PopularBucketItem popularBucketItem) {
      add(popularBucketItem, true);
   }

   void add(PopularBucketItem popularBucketItem, boolean done) {
      bucketInteractor.createPipe()
            .createObservable(new CreateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                  .type(type.getName())
                  .id(String.valueOf(popularBucketItem.getId()))
                  .status(done ? COMPLETED : NEW)
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CreateBucketItemCommand>()
                  .onSuccess(createBucketItemHttpAction -> {
                     BucketItem bucketItem = createBucketItemHttpAction.getResult();
                     analyticsInteractor.analyticsActionPipe()
                           .send(new BucketItemAddedFromPopularAnalyticsAction(query));
                     bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
                           .send(RecentlyAddedBucketsFromPopularCommand.add(bucketItem));
                     view.notifyItemWasAddedToBucketList(bucketItem);
                     view.removeItem(popularBucketItem);
                  }).onFail((failedAction, throwable) -> {
                     handleError(failedAction, throwable);
                     popularBucketItem.setLoading(false);
                     view.notifyItemsChanged();
                  }));
   }

   public void reload() {
      bucketInteractor.getPopularBucketItemsPipe()
            .createObservable(new GetPopularBucketItemsCommand(type))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPopularBucketItemsCommand>()
                  .onStart(command -> view.startLoading())
                  .onFail(this::handleError)
                  .onSuccess(command -> {
                     view.finishLoading();
                     view.setItems(command.getResult());
                  }));
   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      view.finishLoading();
   }

   public interface View extends RxView {
      int getItemsCount();

      void setItems(List<PopularBucketItem> items);

      void removeItem(PopularBucketItem item);

      void notifyItemsChanged();

      void setFilteredItems(List<PopularBucketItem> items);

      void flushFilter();

      void startLoading();

      void finishLoading();

      void notifyItemWasAddedToBucketList(BucketItem bucketItem);
   }
}
