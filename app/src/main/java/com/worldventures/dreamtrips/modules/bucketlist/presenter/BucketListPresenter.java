package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.MarkItemAsDoneHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.common.BucketUtility;
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
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.COMPLETED;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.NEW;

public class BucketListPresenter extends Presenter<BucketListPresenter.View> {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janetApi;
   @Inject BucketInteractor bucketInteractor;
   @Inject LoginInteractor loginInteractor;

   @State BucketItem.BucketType type;
   @State boolean showToDO = true;
   @State boolean showCompleted = true;

   private BucketItem currentItem;

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
      view.bindUntilStop(bucketInteractor.bucketListActionPipe()
            .observeSuccessWithReplay()
            .map(BucketListCommand::getResult)
            .compose(BucketUtility.disJoinByType(type))
            .observeOn(AndroidSchedulers.mainThread())).subscribe(items -> {
         Timber.d("List of buckets updated : " + items.size());
         bucketItems = items;

         view.finishLoading();
         refresh();
      }, throwable -> {
         view.finishLoading();
      });
   }

   @Override
   public void onResume() {
      super.onResume();
      openDetailsIfNeeded(currentItem);
   }

   private void refresh() {
      fillWithItems();
      openDetailsIfNeeded(currentItem);
   }

   private void fillWithItems() {
      if (bucketItems.isEmpty()) {
         filteredItems.clear();
         currentItem = null;
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
         if (filteredItems.isEmpty()) {
            currentItem = null;
         } else if (!filteredItems.contains(currentItem)) {
            currentItem = filteredItems.get(0);
         }
      }

      view.getAdapter().setItems(filteredItems);
      view.checkEmpty(filteredItems.size());
   }

   public void itemClicked(BucketItem bucketItem) {
      if (!isTypeCorrect(bucketItem.getType()) && !bucketItems.contains(bucketItem)) return;

      eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_VIEW));
      currentItem = bucketItem;
      openDetails(currentItem);
   }

   public void onEvent(MarkBucketItemDoneEvent event) {
      if (isTypeCorrect(event.getBucketItem().getType())) {
         BucketItem bucketItem = event.getBucketItem();
         eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_COMPLETE));
         eventBus.cancelEventDelivery(event);
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
      BucketBundle bundle = new BucketBundle();
      bundle.setType(type);
      bundle.setBucketItem(bucketItem);

      view.openDetails(bucketItem);
      // set selected
      Queryable.from(bucketItems).forEachR(item -> item.setSelected(bucketItem.equals(item)));

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
      view.bind(bucketInteractor.marksAsDonePipe()
            .createObservable(new MarkItemAsDoneHttpAction(bucketItem.getUid(), getMarkAsDoneStatus(bucketItem)))
            .observeOn(AndroidSchedulers.mainThread()))
            .subscribe(new ActionStateSubscriber<MarkItemAsDoneHttpAction>().onFail((markItemAsDoneAction, throwable) -> {
               refresh();
               handleError(markItemAsDoneAction, throwable);
            }));
   }

   public void itemMoved(int fromPosition, int toPosition) {
      if (fromPosition == toPosition) {
         return;
      }

      bucketInteractor.bucketListActionPipe()
            .send(BucketListCommand.move(getOriginalPosition(fromPosition), getOriginalPosition(toPosition), type));
   }

   public void addToBucketList(String title) {
      view.bind(bucketInteractor.createPipe()
            .createObservable(new CreateBucketItemHttpAction(ImmutableBucketPostBody.builder()
                  .type(type.getName())
                  .name(title)
                  .status(NEW)
                  .build()))
            .observeOn(AndroidSchedulers.mainThread()))
            .subscribe(new ActionStateSubscriber<CreateBucketItemHttpAction>().onFail(this::handleError));
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
