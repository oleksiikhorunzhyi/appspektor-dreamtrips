package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketViewPagerBundle
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.CurrentOpenTabEventDelegate
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.ViewPhotoEvent
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketCoverBody
import com.worldventures.dreamtrips.social.ui.bucketlist.util.BucketItemInfoHelper
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

open class BucketDetailsBasePresenter<V : BucketDetailsBasePresenter.View<T>, T>(
      @JvmField @field:State internal var type: BucketItem.BucketType,
      @JvmField @field:State internal var bucketItem: BucketItem,
      @JvmField @field:State internal var ownerId: Int) : Presenter<V>() {

   @Inject lateinit var db: SocialSnappyRepository
   @Inject lateinit var bucketInteractor: BucketInteractor
   @Inject lateinit var bucketItemInfoHelper: BucketItemInfoHelper
   @Inject lateinit var currentOpenTabEventDelegate: CurrentOpenTabEventDelegate

   override fun onResume() {
      super.onResume()
      syncUI()
   }

   open fun syncUI() {
      view.apply {
         setBucketItem(bucketItem)
         setStatus(bucketItem.isDone)
         setPeople(bucketItem.friends)
         setTags(bucketItem.bucketTags)
         setTime(bucketItemInfoHelper.getTime(bucketItem))
      }
   }

   protected fun putCoverPhotoAsFirst(photos: MutableList<BucketPhoto>) {
      if (!photos.isEmpty()) {
         val coverIndex = Math.max(photos.indexOf(bucketItem.coverPhoto), 0)
         photos[coverIndex].setIsCover(true)
         photos.add(0, photos.removeAt(coverIndex))
      }
   }

   fun openFullScreen(position: Int) =
      currentOpenTabEventDelegate.replayObservable.take(1).subscribe {
         val isTabletLandscape = view.isTabletLandscape
         if (!isTabletLandscape || isTabletLandscape && type == it) {
            val selectedPhoto = bucketItem.photos[position]
            if (bucketItem.photos.contains(selectedPhoto)) {
               view.openFullscreen(BucketViewPagerBundle(bucketItem, bucketItem.photos.indexOf(selectedPhoto)))
               analyticsInteractor.analyticsActionPipe().send(ViewPhotoEvent(bucketItem.uid))
            }
         }
      }

   fun saveCover(photo: BucketPhoto) =
      bucketInteractor.updatePipe()
            .createObservable(UpdateBucketItemCommand(ImmutableBucketCoverBody.builder()
                  .id(bucketItem.uid)
                  .status(bucketItem.status)
                  .type(bucketItem.type)
                  .coverId(photo.uid)
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onSuccess {
                     bucketItem = it.result
                     syncUI()
                  }
                  .onFail(this::handleError))

   interface View<in T> : RxView {
      fun setBucketItem(bucketItem: BucketItem)

      fun setTime(time: String)

      fun setPeople(people: String)

      fun setTags(tags: String)

      fun setStatus(isCompleted: Boolean)

      fun done()

      fun openFullscreen(data: BucketViewPagerBundle)

      fun setImages(photos: List<T>)
   }
}
