package com.worldventures.dreamtrips.social.ui.bucketlist.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketItemAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketItemViewedAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.TranslateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.TranslationFeedInteractor;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class BucketItemDetailsPresenter extends BucketDetailsBasePresenter<BucketItemDetailsPresenter.View, BucketPhoto>
      implements FeedEntityHolder {

   public BucketItemDetailsPresenter(BucketItem.BucketType type, BucketItem bucketItem, int ownerId) {
      super(type, bucketItem, ownerId);
   }

   @Inject TranslationFeedInteractor translationInteractor;
   @Inject FeedEntityHolderDelegate feedEntityHolderDelegate;

   @Override
   public void onViewTaken() {
      super.takeView(view);
      analyticsInteractor.analyticsActionPipe().send(new BucketItemViewedAction());
      subscribeToTranslations();
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
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
         view.setPlace(bucketItemInfoHelper.getPlace(bucketItem));
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

   void subscribeToTranslations() {
      translationInteractor.translateBucketItemPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<TranslateBucketItemCommand>()
                  .onSuccess(this::translationSucceed)
                  .onFail(this::translationFailed));
   }

   void translationSucceed(TranslateBucketItemCommand command) {
      bucketItem = command.getResult();
      view.setBucketItem(bucketItem);
   }

   void translationFailed(TranslateBucketItemCommand command, Throwable e) {
      handleError(command, e);
      view.setBucketItem(bucketItem);
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

         analyticsInteractor.analyticsActionPipe().send(BucketItemAction.markAsDone(bucketItem.getUid()));
      }
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      if (updatedFeedEntity.equals(bucketItem)) {
         bucketItem = (BucketItem) updatedFeedEntity;
         if (bucketItem.getOwner() == null) {
            bucketItem.setOwner(updatedFeedEntity.getOwner());
         }
         syncUI();
      }
   }

   @Override
   public void deleteFeedEntity(FeedEntity deletedFeedEntity) {
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