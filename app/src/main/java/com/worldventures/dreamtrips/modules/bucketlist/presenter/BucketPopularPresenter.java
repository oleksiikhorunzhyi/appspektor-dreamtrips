package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.techery.spares.annotations.State;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.bucketlist.analytics.BucketItemAddedFromPopularAnalyticsAction;
import com.worldventures.dreamtrips.modules.bucketlist.analytics.BucketPopularTabViewAnalyticsAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetPopularBucketItemSuggestionsCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetPopularBucketItemsCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.COMPLETED;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.NEW;

public class BucketPopularPresenter extends Presenter<BucketPopularPresenter.View> {

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
      if (view.getAdapter().getCount() == 0) reload();
   }

   public void onSearch(String query) {
      this.query = query;
      if (query.length() > 2) {
         bucketInteractor.getPopularBucketItemSuggestionsPipe()
               .createObservable(new GetPopularBucketItemSuggestionsCommand(type, query))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<GetPopularBucketItemSuggestionsCommand>()
                     .onStart(command -> view.startLoading())
                     .onFail(this::handleError)
                     .onSuccess(command -> onSearchSucceed(command.getResult())));
      }
   }

   public void onSelected() {
      analyticsInteractor.analyticsActionPipe().send(new BucketPopularTabViewAnalyticsAction(type));
   }

   private void onSearchSucceed(List<PopularBucketItem> items) {
      if (view != null) {
         view.finishLoading();
         view.getAdapter().setFilteredItems(items);
      }
   }

   public void searchClosed() {
      view.getAdapter().flushFilter();
   }

   public void onAdd(PopularBucketItem popularBucketItem) {
      add(popularBucketItem, false);
   }

   public void onDone(PopularBucketItem popularBucketItem) {
      add(popularBucketItem, true);
   }

   private void add(PopularBucketItem popularBucketItem, boolean done) {
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
                     analyticsInteractor.analyticsActionPipe().send(new BucketItemAddedFromPopularAnalyticsAction(query));
                     bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
                           .send(RecentlyAddedBucketsFromPopularCommand.add(bucketItem));
                     view.notifyItemWasAddedToBucketList(bucketItem);
                     view.getAdapter().remove(popularBucketItem);
                  }).onFail((failedAction, throwable) -> {
                     handleError(failedAction, throwable);
                     popularBucketItem.setLoading(false);
                     view.getAdapter().notifyDataSetChanged();
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
                     view.getAdapter().clear();
                     view.getAdapter().addItems(command.getResult());
                     view.getAdapter().notifyDataSetChanged();
                  }));
   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      view.finishLoading();
   }

   public interface View extends RxView {
      FilterableArrayListAdapter<PopularBucketItem> getAdapter();

      void startLoading();

      void finishLoading();

      void notifyItemWasAddedToBucketList(BucketItem bucketItem);
   }
}
