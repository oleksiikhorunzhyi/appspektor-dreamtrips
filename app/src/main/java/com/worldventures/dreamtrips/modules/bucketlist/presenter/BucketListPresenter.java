package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.analytics.BucketItemAddedAnalyticsAction;
import com.worldventures.dreamtrips.modules.bucketlist.analytics.BucketTabViewAnalyticsAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.common.BucketUtility;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketPostBody;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.SuggestionLoader;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.State;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.COMPLETED;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.NEW;

public class BucketListPresenter extends Presenter<BucketListPresenter.View> {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janetApi;
   @Inject BucketInteractor bucketInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   @State BucketItem.BucketType type;
   @State boolean showToDO = true;
   @State boolean showCompleted = true;

   private BucketItem lastOpenedBucketItem;

   private List<BucketItem> bucketItems = new ArrayList<>();

   private List<BucketItem> filteredItems = new ArrayList<>();

   public BucketListPresenter(BucketItem.BucketType type) {
      this.type = type;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      TrackingHelper.bucketList(getAccountUserId());
   }

   @Override
   public void onStart() {
      super.onStart();
      view.startLoading();
   }

   @Override
   public void onResume() {
      super.onResume();
      bucketInteractor.bucketListActionPipe()
            .observeWithReplay()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(new ActionStateSubscriber<BucketListCommand>()
                  .onSuccess(bucketListCommand -> onSuccessLoadingBucketList(bucketListCommand.getResult()))
                  .onFail((bucketListCommand, throwable) -> {
                     view.finishLoading();
                     handleError(bucketListCommand, throwable);
                  }));
   }

   private void onSuccessLoadingBucketList(List<BucketItem> newItems) {
      Observable.just(newItems)
            .compose(BucketUtility.disJoinByType(type))
            .subscribe(items -> {
               bucketItems = items;
               view.finishLoading();
               refresh();
            });
   }

   public void onSelected() {
      analyticsInteractor.analyticsActionPipe().send(new BucketTabViewAnalyticsAction(type));
   }

   private void refresh() {
      if (bucketItems.isEmpty()) {
         filteredItems.clear();
      } else {
         filteredItems.clear();
         if (showToDO) {
            Collection<BucketItem> toDo = Queryable.from(bucketItems)
                  .filter((bucketItem) -> !bucketItem.isDone())
                  .toList();
            filteredItems.addAll(toDo);
         }
         view.putCategoryMarker(filteredItems.size());
         if (showCompleted) {
            Collection<BucketItem> done = Queryable.from(bucketItems).filter(BucketItem::isDone).toList();
            filteredItems.addAll(done);
         }
         //
         if (!filteredItems.isEmpty() && !filteredItems.get(0).equals(lastOpenedBucketItem)) {
            openDetailsIfNeeded(filteredItems.get(0));
         }
      }

      view.getAdapter().setItems(filteredItems);
      view.checkEmpty(filteredItems.size());
   }

   public void itemClicked(BucketItem bucketItem) {
      if (!isTypeCorrect(bucketItem.getType()) && !bucketItems.contains(bucketItem)) return;

      TrackingHelper.actionBucketItem(TrackingHelper.ATTRIBUTE_VIEW, bucketItem.getUid());
      openDetails(bucketItem);
   }

   public void itemDoneClicked(BucketItem bucketItem) {
      if (isTypeCorrect(bucketItem.getType())) {
         TrackingHelper.actionBucketItem(TrackingHelper.ATTRIBUTE_COMPLETE, bucketItem.getUid());
         markAsDone(bucketItem);
      }
   }

   private boolean isTypeCorrect(String bucketType) {
      return bucketType.equalsIgnoreCase(type.getName());
   }

   private void openDetailsIfNeeded(BucketItem item) {
      if (view == null || !view.isTabletLandscape()) return;
      //
      if (item != null) openDetails(item);
      else {
         view.hideDetailContainer();
      }
   }

   private void openDetails(BucketItem bucketItem) {
      lastOpenedBucketItem = bucketItem;

      Queryable.from(bucketItems).forEachR(item -> item.setSelected(bucketItem.equals(item)));

      view.openDetails(bucketItem);
      view.getAdapter().notifyDataSetChanged();
   }

   public void popularClicked() {
      BucketBundle bundle = new BucketBundle();
      bundle.setType(type);
      view.openPopular(bundle);
   }

   public void reloadWithFilter(int filterId) {
      switch (filterId) {
         case R.id.action_show_all:
            showToDO = true;
            showCompleted = true;
            break;
         case R.id.action_show_to_do:
            showToDO = true;
            showCompleted = false;
            break;
         case R.id.action_show_completed:
            showToDO = false;
            showCompleted = true;
            break;
         default:
            break;
      }
      refresh();
   }

   private void markAsDone(BucketItem bucketItem) {
      bucketInteractor.updatePipe()
            .createObservable(new UpdateBucketItemCommand(ImmutableBucketBodyImpl.builder().id(bucketItem.getUid())
                  .status(getMarkAsDoneStatus(bucketItem)).build()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>().onFail((markItemAsDoneAction, throwable) -> {
               refresh();
               handleError(markItemAsDoneAction, throwable);
            }));
   }

   public void itemMoved(int fromPosition, int toPosition) {
      if (fromPosition == toPosition) return;
      bucketInteractor.bucketListActionPipe()
            .send(BucketListCommand.move(getOriginalPosition(fromPosition), getOriginalPosition(toPosition), type));
   }

   public void addToBucketList(String title) {
      bucketInteractor.createPipe()
            .createObservable(new CreateBucketItemCommand(ImmutableBucketPostBody
                  .builder()
                  .type(type.getName())
                  .name(title)
                  .status(NEW)
                  .build()))
            .subscribe(new ActionStateSubscriber<CreateBucketItemCommand>()
                  .onSuccess(command -> analyticsInteractor.analyticsActionPipe()
                        .send(new BucketItemAddedAnalyticsAction(title)))
                  .onFail(this::handleError));
   }

   public boolean isShowToDO() {
      return showToDO;
   }

   public boolean isShowCompleted() {
      return showCompleted;
   }

   public AutoCompleteAdapter.Loader getSuggestionLoader() {
      return new SuggestionLoader(type, janetApi);
   }

   private int getOriginalPosition(int filteredPosition) {
      return bucketItems.indexOf(filteredItems.get(filteredPosition));
   }

   private String getMarkAsDoneStatus(BucketItem item) {
      return item.isDone() ? NEW : COMPLETED;
   }

   public void trackAnalyticsActionBucket(String actionAttribute) {
      TrackingHelper.actionBucket(actionAttribute, type.getAnalyticsName());
   }

   public interface View extends RxView {

      BaseArrayListAdapter<BucketItem> getAdapter();

      void startLoading();

      void finishLoading();

      void showDetailsContainer();

      void hideDetailContainer();

      void putCategoryMarker(int position);

      void checkEmpty(int count);

      void openDetails(BucketItem bucketItem);

      void openPopular(BucketBundle args);
   }
}
