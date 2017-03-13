package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.TranslateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View, BucketPhoto> {

   public BucketItemDetailsPresenter(BucketBundle bundle) {
      super(bundle);
   }

   @Inject TranslationFeedInteractor translationInteractor;
   @Inject BucketInteractor bucketInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      TrackingHelper.bucketItemView(type.getName(), bucketItem.getUid());
      Observable.merge(bucketInteractor.updatePipe().observeSuccess().map(UpdateBucketItemCommand::getResult),
            bucketInteractor.deleteItemPhotoPipe().observeSuccess().map(DeleteItemPhotoCommand::getResult),
            bucketInteractor.addBucketItemPhotoPipe().observeSuccess().map(AddBucketItemPhotoCommand::getResult)
                  .map(bucketItemBucketPhotoModelPair -> bucketItemBucketPhotoModelPair.first))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(item -> {
               bucketItem = item;
               syncUI();
            });
      subscribeToItemDoneEvents();
      subscribeToTranslations();
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
            view.setCategory(bucketItem.getCategoryName());
         }
         view.setPlace(BucketItemInfoUtil.getPlace(bucketItem));
         view.setupDiningView(bucketItem.getDining());
         view.setGalleryEnabled(photos != null && !photos.isEmpty());
      }
   }

   public void onTranslateClicked() {
      if (bucketItem.isTranslated()) {
         bucketItem.setTranslated(false);
         view.setBucketItem(bucketItem);
      } else {
         translationInteractor.translateBucketItemPipe().send(new TranslateBucketItemCommand(bucketItem));
      }
   }

   private void subscribeToItemDoneEvents() {
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

   private void subscribeToTranslations() {
      translationInteractor.translateBucketItemPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<TranslateBucketItemCommand>()
                  .onSuccess(this::translationSucceed)
                  .onFail(this::translationFailed));
   }

   private void translationSucceed(TranslateBucketItemCommand command) {
      bucketItem = command.getResult();
      view.setBucketItem(bucketItem);
   }

   private void translationFailed(TranslateBucketItemCommand command, Throwable e) {
      handleError(command, e);
      view.setBucketItem(bucketItem);
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
         bucketInteractor.updatePipe().createObservable(new UpdateBucketItemCommand(ImmutableBucketBodyImpl
               .builder()
               .id(bucketItem.getUid())
               .status(getStatus(status))
               .build()))
               .compose(bindViewToMainComposer())
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

   public interface View extends BucketDetailsBasePresenter.View<BucketPhoto> {
      void setCategory(String category);

      void setPlace(String place);

      void disableMarkAsDone();

      void enableMarkAsDone();

      void setGalleryEnabled(boolean enabled);

      void setupDiningView(DiningItem diningItem);
   }
}