package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.model.DiningItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketItemAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketItemViewedAction
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.TranslateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.TranslationFeedInteractor
import io.techery.janet.helper.ActionStateSubscriber
import rx.functions.Action2
import javax.inject.Inject

open class BucketItemDetailsPresenter(type: BucketItem.BucketType, bucketItem: BucketItem, ownerId: Int)
   : BucketDetailsBasePresenter<BucketItemDetailsPresenter.View, BucketPhoto>(type, bucketItem, ownerId), FeedEntityHolder {

   @Inject internal lateinit var translationInteractor: TranslationFeedInteractor
   @Inject internal lateinit var feedEntityHolderDelegate: FeedEntityHolderDelegate

   override fun onViewTaken() {
      super.takeView(view)
      analyticsInteractor.analyticsActionPipe().send(BucketItemViewedAction())
      subscribeToTranslations()
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer<Any>(), Action2(this::handleError))
   }

   override fun syncUI() {
      super.syncUI()
      val photos = bucketItem.photos
      if (!photos.isEmpty()) {
         putCoverPhotoAsFirst(photos)
         view.setImages(photos)
      }
      view.apply {
         setCategory(bucketItem.categoryName)
         setPlace(bucketItemInfoHelper.getPlace(bucketItem))
         setupDiningView(bucketItem.dining)
         setGalleryEnabled(!photos.isEmpty())
      }
   }

   fun onTranslateClicked() {
      if (bucketItem.isTranslated) {
         bucketItem.isTranslated = false
         view.setBucketItem(bucketItem)
      } else {
         translationInteractor.translateBucketItemPipe().send(TranslateBucketItemCommand(bucketItem))
      }
   }

   internal fun subscribeToTranslations() =
      translationInteractor.translateBucketItemPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<TranslateBucketItemCommand>()
                  .onSuccess(this::translationSucceed)
                  .onFail { command, e -> translationFailed(command, e) })

   internal fun translationSucceed(command: TranslateBucketItemCommand) {
      bucketItem = command.result
      view.setBucketItem(bucketItem)
   }

   internal fun translationFailed(command: TranslateBucketItemCommand, e: Throwable) {
      handleError(command, e)
      view.setBucketItem(bucketItem)
   }

   fun onStatusUpdated(status: Boolean) {
      if (status == bucketItem.isDone) {
         return
      }
      view.disableMarkAsDone()
      bucketInteractor.updatePipe().createObservable(UpdateBucketItemCommand(ImmutableBucketBodyImpl
            .builder()
            .id(bucketItem.uid)
            .status(getStatus(status))
            .build()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onSuccess { view.enableMarkAsDone() }
                  .onFail { action, throwable ->
                     handleError(action, throwable)
                     view.setStatus(bucketItem.isDone)
                     view.enableMarkAsDone()
                  })

      analyticsInteractor.analyticsActionPipe().send(BucketItemAction.markAsDone(bucketItem.uid))
   }

   override fun updateFeedEntity(updatedFeedEntity: FeedEntity) {
      if (updatedFeedEntity == bucketItem) {
         if (updatedFeedEntity is BucketItem) {
            bucketItem = updatedFeedEntity
            if (bucketItem.owner == null) {
               bucketItem.owner = updatedFeedEntity.getOwner()
            }
            syncUI()
         }
      }
   }

   override fun deleteFeedEntity(deletedFeedEntity: FeedEntity) {}

   private fun getStatus(status: Boolean): String = if (status) BucketItem.COMPLETED else BucketItem.NEW

   interface View : BucketDetailsBasePresenter.View<BucketPhoto> {
      fun setCategory(category: String)

      fun setPlace(place: String?)

      fun disableMarkAsDone()

      fun enableMarkAsDone()

      fun setGalleryEnabled(enabled: Boolean)

      fun setupDiningView(diningItem: DiningItem?)
   }
}
