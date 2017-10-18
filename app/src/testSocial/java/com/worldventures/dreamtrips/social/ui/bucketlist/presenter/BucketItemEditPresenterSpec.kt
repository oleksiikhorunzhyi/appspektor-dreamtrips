package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import android.support.v4.util.Pair
import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.dreamtrips.common.Injector
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.AddBucketItemPhotoCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand
import io.techery.janet.CancelException
import io.techery.janet.command.test.Contract
import junit.framework.Assert.assertFalse
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.it

class BucketItemEditPresenterSpec : BucketDetailsBasePresenterSpec<BucketItemEditPresenter, BucketItemEditPresenter.View,
      BucketItemEditPresenterSpec.DetailsTestBody>(DetailsTestBody()) {

   class DetailsTestBody: TestBody<BucketItemEditPresenter, BucketItemEditPresenter.View>() {

      override fun describeTest(): String = BucketItemDetailsPresenter::class.java.simpleName

      override fun createPresenter(): BucketItemEditPresenter = BucketItemEditPresenter(BucketItem.BucketType.ACTIVITY, spy(stubBucketItem()), 11)

      override fun createView(): BucketItemEditPresenter.View = mock()

      override fun onSetupInjector(injector: Injector, pipeCreator: SessionActionPipeCreator) {

      }

      override fun onResumeTestSetup() {
         whenever(socialSnappy.getBucketListCategories()).thenReturn(emptyList())
         setup(Contract.of(MergeBucketItemPhotosWithStorageCommand::class.java).result(emptyList<EntityStateHolder<BucketPhoto>>()))
      }

      override fun getMainTestSuite(): Spec.() -> Unit {
         return {
            it("should subscribe to new photos on take view") {
               setup()
               doNothing().whenever(presenter).subscribeToAddingPhotos()

               presenter.onViewTaken()

               verify(presenter).subscribeToAddingPhotos()
            }

            it("should merge bucket items with storage on sync ui") {
               setup(Contract.of(MergeBucketItemPhotosWithStorageCommand::class.java).result(emptyList<EntityStateHolder<BucketPhoto>>()))

               presenter.onResume()

               verify(presenter).mergeBucketItemPhotosWithStorage()
               verify(view).setImages(any())
            }

            it("should update bucket on done") {
               setup(Contract.of(UpdateBucketItemCommand::class.java).result(presenter.bucketItem))
               whenever(view.tags).thenReturn(emptyList())
               whenever(view.people).thenReturn(emptyList())

               presenter.saveItem()

               verify(view).showLoading()
               verify(view).done()
               assertFalse(presenter.savingItem)
            }

            it("should delete photo properly") {
               setup(Contract.of(DeleteItemPhotoCommand::class.java).result(presenter.bucketItem))
               val bucketPhoto = stubBucketPhoto()
               val bucketItem = presenter.bucketItem
               whenever(bucketItem.photos).thenReturn(listOf(bucketPhoto))

               presenter.deletePhotoRequest(bucketPhoto)

               verify(view).deleteImage(any())
            }

            it("should process deleting photos error properly") {
               setup(Contract.of(DeleteItemPhotoCommand::class.java).exception(Exception()))
               val bucketPhoto = stubBucketPhoto()
               val bucketItem = presenter.bucketItem
               whenever(bucketItem.photos).thenReturn(listOf(bucketPhoto))

               presenter.deletePhotoRequest(bucketPhoto)

               verify(presenter).handleError(any(), any())
            }

            it("should cancel upload if uploading is in progress") {
               setup()
               val stateHolder = EntityStateHolder.create(stubBucketPhoto(), EntityStateHolder.State.PROGRESS)
               doNothing().whenever(presenter).cancelUpload(any())

               presenter.onPhotoCellClicked(stateHolder)

               verify(view).deleteImage(any())
               verify(presenter).cancelUpload(any())
            }

            it("should start upload if uploading is failed") {
               setup()
               val stateHolder = EntityStateHolder.create(stubBucketPhoto(), EntityStateHolder.State.FAIL)
               doNothing().whenever(presenter).startUpload(any())

               presenter.onPhotoCellClicked(stateHolder)

               verify(view).deleteImage(any())
               verify(presenter).startUpload(any())
            }

            it("should refresh view when uploading is finished") {
               val bucket = presenter.bucketItem
               val photo = stubBucketPhoto()
               setup(Contract.of(AddBucketItemPhotoCommand::class.java).result(Pair(bucket, photo)))
               val addBucketItemPhotoCommand = AddBucketItemPhotoCommand(bucket, "")
               presenter.operationList.add(addBucketItemPhotoCommand)

               presenter.subscribeToAddingPhotos()
               bucketInteractor.addBucketItemPhotoPipe().send(addBucketItemPhotoCommand)

               verify(view).changeItemState(any())
               assertFalse(presenter.operationList.contains(addBucketItemPhotoCommand))
            }
         }
      }

      private fun stubBucketPhoto(): BucketPhoto = BucketPhoto().apply {
         uid = "11"
         setUrl("")
      }
   }
}