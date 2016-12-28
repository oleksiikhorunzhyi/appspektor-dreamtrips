package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View> {

   public BucketItemDetailsPresenter(BucketBundle bundle) {
      super(bundle);
   }

   @Inject BucketInteractor bucketInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      TrackingHelper.bucketItemView(type.getName(), bucketItem.getUid());

      view.bind(Observable.merge(bucketInteractor.updatePipe()
            .observeSuccess()
            .map(UpdateBucketItemCommand::getResult), bucketInteractor.deleteItemPhotoPipe()
            .observeSuccess()
            .map(DeleteItemPhotoCommand::getResult), bucketInteractor.addBucketItemPhotoPipe()
            .observeSuccess()
            .map(AddBucketItemPhotoCommand::getResult)
            .map(bucketItemBucketPhotoModelPair -> bucketItemBucketPhotoModelPair.first))
            .observeOn(AndroidSchedulers.mainThread())).subscribe(item -> {
         bucketItem = item;
         syncUI();
      });
      subscribeToItemDoneEvents();
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

   public void subscribeToItemDoneEvents() {
      bucketInteractor.updatePipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .map(UpdateBucketItemCommand::getResult)
            .filter(bucketItem -> bucketItem.equals(this.bucketItem))
            .subscribe(item -> {
                  updateBucketItem(item);
                  syncUI();
            });
   }

   public void onEvent(FeedEntityChangedEvent event) {
      if (event.getFeedEntity().equals(bucketItem)) {
         updateBucketItem((BucketItem) event.getFeedEntity());
         syncUI();
      }
   }

   public void onStatusUpdated(boolean status) {
      if (bucketItem != null && status != bucketItem.isDone()) {
         view.disableMarkAsDone();

         view.bind(bucketInteractor.updatePipe().createObservable(new UpdateBucketItemCommand(ImmutableBucketBodyImpl
               .builder()
               .id(bucketItem.getUid())
               .status(getStatus(status))
               .build()))
               .observeOn(AndroidSchedulers.mainThread()))
               .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>()
                     .onSuccess(updateItemHttpAction -> view.enableMarkAsDone())
                     .onFail((action, throwable) -> {
                        handleError(action, throwable);
                        view.setStatus(bucketItem.isDone());
                        view.enableMarkAsDone();
                     }));

         TrackingHelper.actionBucketItem(TrackingHelper.ATTRIBUTE_MARK_AS_DONE, bucketItem.getUid());
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