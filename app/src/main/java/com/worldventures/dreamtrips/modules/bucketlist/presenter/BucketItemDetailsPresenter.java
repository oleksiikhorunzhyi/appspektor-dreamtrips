package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemShared;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

   public BucketItemDetailsPresenter(BucketBundle bundle) {
      super(bundle);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      TrackingHelper.bucketItemView(type.getName(), bucketItem.getUid());

      view.bind(Observable.merge(bucketInteractor.updatePipe()
            .observeSuccess()
            .map(UpdateItemHttpAction::getResponse), bucketInteractor.deleteItemPhotoPipe()
            .observeSuccess()
            .map(DeleteItemPhotoCommand::getResult), bucketInteractor.addBucketItemPhotoPipe()
            .observeSuccess()
            .map(AddBucketItemPhotoCommand::getResult)
            .map(bucketItemBucketPhotoModelPair -> bucketItemBucketPhotoModelPair.first))
            .observeOn(AndroidSchedulers.mainThread())).subscribe(item -> {
         bucketItem = item;
         syncUI();
      });
   }

   @Override
   protected void syncUI() {
      super.syncUI();
      if (bucketItem != null) {
         List photos = bucketItem.getPhotos();

         if (photos != null) {
            putCoverPhotoAsFirst(photos);
            view.setImages(photos);
         }

         if (!TextUtils.isEmpty(bucketItem.getType())) {
            String s = bucketItem.getCategoryName();
            view.setCategory(s);
         }
         view.setPlace(BucketItemInfoUtil.getPlace(bucketItem));
         view.setupDiningView(bucketItem.getDining());
         view.setGalleryEnabled(photos != null && !photos.isEmpty());
      }
   }

   public void onEvent(MarkBucketItemDoneEvent event) {
      if (event.getBucketItem().equals(bucketItem)) {
         updateBucketItem(event.getBucketItem());
         syncUI();
      }
   }

   public void onEvent(FeedEntityChangedEvent event) {
      if (event.getFeedEntity().equals(bucketItem)) {
         updateBucketItem((BucketItem) event.getFeedEntity());
         syncUI();
      }
   }

   public void onEvent(@SuppressWarnings("unused") BucketItemShared event) {
      eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_SHARE));
   }

   public void onStatusUpdated(boolean status) {
      if (bucketItem != null && status != bucketItem.isDone()) {
         view.disableMarkAsDone();

         view.bind(bucketInteractor.updatePipe().createObservable(new UpdateItemHttpAction(ImmutableBucketBodyImpl
               .builder()
               .id(bucketItem.getUid())
               .status(getStatus(status))
               .build()))
               .observeOn(AndroidSchedulers.mainThread()))
               .subscribe(new ActionStateSubscriber<UpdateItemHttpAction>()
                     .onSuccess(updateItemHttpAction -> view.enableMarkAsDone())
                     .onFail((action, throwable) -> {
                        handleError(action, throwable);
                        view.setStatus(bucketItem.isDone());
                        view.enableMarkAsDone();
                     }));

         eventBus.post(new BucketItemAnalyticEvent(bucketItem.getUid(), TrackingHelper.ATTRIBUTE_MARK_AS_DONE));
      }
   }

   private void updateBucketItem(BucketItem updatedItem) {
      BucketItem tempItem = bucketItem;
      bucketItem = updatedItem;
      if (bucketItem.getOwner() == null) {
         bucketItem.setOwner(tempItem.getOwner());
      }
   }

   @NonNull
   private String getStatus(boolean status) {
      return status ? BucketItem.COMPLETED : BucketItem.NEW;
   }

   public interface View extends BucketDetailsBasePresenter.View {
      void setCategory(String category);

      void setPlace(String place);

      void disableMarkAsDone();

      void enableMarkAsDone();

      void setGalleryEnabled(boolean enabled);

      void setupDiningView(DiningItem diningItem);
   }
}