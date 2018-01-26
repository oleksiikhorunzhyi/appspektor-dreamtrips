package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import android.support.v4.util.Pair
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertFalse

class BucketItemEditPresenterSpec : BucketDetailsBasePresenterSpec(BucketItemEditTestSuite()) {

   class BucketItemEditTestSuite : BucketBaseDetailsTestSuite<BucketItemEditComponents>(BucketItemEditComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Bucket Item Edit Presenter") {

               super.specs().invoke(this)

               it("should load categories") {
                  init(defaultMockContracts())
                  linkPresenterAndView()

                  presenter.loadCategories()

                  verify(view).setCategoryItems(any(), anyOrNull())
               }

               it("should set images on sync ui") {
                  val contracts = mutableListOf<Contract>()
                  contracts.addAll(defaultMockContracts())
                  contracts.add(Contract.of(MergeBucketItemPhotosWithStorageCommand::class.java)
                        .result(emptyList<EntityStateHolder<BucketPhoto>>()))
                  init(contracts)
                  linkPresenterAndView()

                  presenter.onResume()

                  verify(view).setImages(any())
               }

               it("should update bucket on done") {
                  initWithContract((Contract.of(UpdateBucketItemCommand::class.java).result(bucketItem)))
                  linkPresenterAndView()

                  val presenter = presenter
                  val view = view
                  whenever(view.tags).thenReturn(emptyList())
                  whenever(view.people).thenReturn(emptyList())

                  presenter.saveItem()

                  verify(view).showLoading()
                  verify(view).done()
                  assertFalse(presenter.savingItem)
               }

               it("should delete photo properly") {
                  initWithContract(Contract.of(DeleteItemPhotoCommand::class.java).result(bucketItem))
                  linkPresenterAndView()

                  val bucketPhoto = stubBucketPhoto()
                  val bucketItem = stubBucketItem()
                  bucketItem.photos = listOf(bucketPhoto)
                  presenter.bucketItem = bucketItem

                  presenter.deletePhotoRequest(bucketPhoto)

                  verify(view).deleteImage(any())
               }

               it("should delete image if uploading is in progress") {
                  init()
                  linkPresenterAndView()
                  val stateHolder = EntityStateHolder.create(stubBucketPhoto(), EntityStateHolder.State.PROGRESS)

                  presenter.onPhotoCellClicked(stateHolder)

                  verify(view).deleteImage(any())
               }

               it("should delete image if uploading is failed and start upload") {
                  val contract = Contract.of(AddBucketItemPhotoCommand::class.java)
                        .result(Pair(stubBucketItem(), stubBucketPhoto()))
                  initWithContract(contract)
                  linkPresenterAndView()
                  val stateHolder = EntityStateHolder.create(stubBucketPhoto(), EntityStateHolder.State.FAIL)
                  val testSubscriber = TestSubscriber<AddBucketItemPhotoCommand>()
                  bucketInteractor.addBucketItemPhotoPipe().observeSuccess().subscribe(testSubscriber)

                  presenter.onPhotoCellClicked(stateHolder)

                  verify(view).deleteImage(any())
                  testSubscriber.assertValueCount(1)
               }

               it("should refresh view when uploading is finished") {
                  val bucket = bucketItem
                  val photo = stubBucketPhoto()
                  initWithContract(Contract.of(AddBucketItemPhotoCommand::class.java).result(Pair(bucket, photo)))
                  linkPresenterAndView()
                  val addBucketItemPhotoCommand = AddBucketItemPhotoCommand(bucket, "")
                  presenter.operationList.add(addBucketItemPhotoCommand)

                  presenter.subscribeToAddingPhotos()
                  bucketInteractor.addBucketItemPhotoPipe().send(addBucketItemPhotoCommand)

                  verify(view).changeItemState(any())
                  assertFalse(presenter.operationList.contains(addBucketItemPhotoCommand))
               }
            }
         }
      }
   }

   class BucketItemEditComponents : BucketBaseDetailsComponents<BucketItemEditPresenter, BucketItemEditPresenter.View>() {

      val bucketItem = spy(stubBucketItem())

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         view = mock()
         presenter = spy(BucketItemEditPresenter(BucketItem.BucketType.ACTIVITY, bucketItem, 11))

         injector.inject(presenter)
      }

      fun stubBucketPhoto(): BucketPhoto = BucketPhoto().apply {
         uid = "11"
         setUrl("")
      }

      fun defaultMockContracts(): List<Contract> {
         val categoriesList = listOf(CategoryItem(1, "Test"))

         return listOf(Contract.of(GetCategoriesCommand::class.java).result(categoriesList),
               Contract.of(MergeBucketItemPhotosWithStorageCommand::class.java).result(emptyList<EntityStateHolder<BucketPhoto>>()))
      }
   }
}
