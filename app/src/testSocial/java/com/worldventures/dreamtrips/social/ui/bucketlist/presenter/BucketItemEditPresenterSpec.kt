package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import android.support.v4.util.Pair
import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.AddBucketItemPhotoCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetCategoriesCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand
import io.techery.janet.command.test.Contract
import junit.framework.Assert.assertFalse
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import rx.lang.kotlin.TestSubject
import rx.observers.TestSubscriber
import java.util.Collections

class BucketItemEditPresenterSpec : BucketDetailsBasePresenterSpec<BucketItemEditPresenter, BucketItemEditPresenter.View,
      BucketItemEditPresenterSpec.DetailsTestBody>(DetailsTestBody()) {

   class DetailsTestBody : TestBody<BucketItemEditPresenter, BucketItemEditPresenter.View>() {

      override fun describeTest(): String = BucketItemDetailsPresenter::class.java.simpleName

      override fun createPresenter(): BucketItemEditPresenter = BucketItemEditPresenter(BucketItem.BucketType.ACTIVITY, spy(stubBucketItem()), 11)

      override fun createView(): BucketItemEditPresenter.View = mock()

      override fun onSetupInjector(injector: Injector, pipeCreator: SessionActionPipeCreator) {

      }

      override fun onResumeTestSetup() {
         setup(defaultMockContracts())
      }

      override fun getMainTestSuite(): Spec.() -> Unit {
         return {
            it("should load categories") {
               setup(defaultMockContracts())

               presenter.loadCategories()

               verify(view).setCategoryItems(any(), anyOrNull())
            }

            it("should set images on sync ui") {
               val contracts = mutableListOf<Contract>()
               contracts.addAll(defaultMockContracts())
               contracts.add(Contract.of(MergeBucketItemPhotosWithStorageCommand::class.java)
                     .result(emptyList<EntityStateHolder<BucketPhoto>>()))
               setup(contracts)

               presenter.onResume()

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
               val bucketItem = stubBucketItem()
               bucketItem.photos = listOf(bucketPhoto)
               presenter.bucketItem = bucketItem

               presenter.deletePhotoRequest(bucketPhoto)

               verify(view).deleteImage(any())
            }

            it("should delete image if uploading is in progress") {
               setup()
               val stateHolder = EntityStateHolder.create(stubBucketPhoto(), EntityStateHolder.State.PROGRESS)

               presenter.onPhotoCellClicked(stateHolder)

               verify(view).deleteImage(any())
            }

            it("should delete image if uploading is failed and start upload") {
               setup(Contract.of(AddBucketItemPhotoCommand::class.java).result(Pair(stubBucketItem(), stubBucketPhoto())))
               val stateHolder = EntityStateHolder.create(stubBucketPhoto(), EntityStateHolder.State.FAIL)
               val testSubscriber = TestSubscriber<AddBucketItemPhotoCommand>()
               bucketInteractor.addBucketItemPhotoPipe().observeSuccess().subscribe(testSubscriber)

               presenter.onPhotoCellClicked(stateHolder)

               verify(view).deleteImage(any())
               testSubscriber.assertValueCount(1)
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

      private fun defaultMockContracts(): List<Contract> {
         val categoriesList = listOf(CategoryItem(1, "Test"))

         return listOf(Contract.of(GetCategoriesCommand::class.java).result(categoriesList),
               Contract.of(MergeBucketItemPhotosWithStorageCommand::class.java).result(emptyList<EntityStateHolder<BucketPhoto>>()))
      }
   }
}
