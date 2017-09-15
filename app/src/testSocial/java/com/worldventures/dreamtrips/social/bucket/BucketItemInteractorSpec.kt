package com.worldventures.dreamtrips.social.bucket

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.AssertUtil.assertStatusCount
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized
import com.worldventures.dreamtrips.api.bucketlist.model.BucketStatus
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketItemSocialized
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketPhoto
import com.worldventures.dreamtrips.api.messenger.model.response.ImmutableShortUserProfile
import com.worldventures.dreamtrips.api.session.model.ImmutableAvatar
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImage
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImageResponse
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.*
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.*
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketBody
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketPostBody
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.BucketListDiskStorage
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.BucketMemoryStorage
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder.State
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder.create
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import io.techery.janet.ActionState
import io.techery.janet.http.annotations.HttpAction.Method
import io.techery.janet.http.test.MockHttpActionService
import junit.framework.Assert
import org.assertj.core.util.Lists
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import java.util.*

typealias ApiBucketPhoto = com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto
typealias ApiBucketType = com.worldventures.dreamtrips.api.bucketlist.model.BucketType

class BucketItemInteractorSpec : BucketInteractorBaseSpec({
   describe("bucket actions on item") {
      setup()

      beforeEachTest {
         doReturn(mutableListOf(testBucketItem, testBucketItem2))
               .whenever(mockMemoryStorage).get(any())
      }

      context("item creation") {

         it("should create item and add created item into result list") {
            val title = "Test"
            val type = BucketType.LOCATION.getName()
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            whenever(testBucketItem.name).thenReturn(title)
            whenever(testBucketItem.type).thenReturn(type)
            testBucketItemApi = getStubbedApiBucketSocialized()
                  .type(ApiBucketType.LOCATION)
                  .name(title)
                  .status(BucketStatus.NEW)
                  .build()
            setup()

            bucketInteractor.bucketListActionPipe()
                  .observe()
                  .subscribe(testListSubscriber)

            assertActionSuccess(subscribeAddBucketItem(ImmutableBucketPostBody.builder()
                  .type(type)
                  .name(title)
                  .status(NEW)
                  .build())) {
               val item = it.result
               item.name == title && item.type == type
            }
            assertBucketWasAddedInList(testListSubscriber)
         }


         context("create bucket item from popular") {

            it("should create item from popular and add it to list") {
               val type = BucketType.LOCATION.getName()
               val popularId = 123
               val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

               whenever(testBucketItem.type).thenReturn(type)
               whenever(testBucketItem.status).thenReturn(NEW)
               testBucketItemApi = getStubbedApiBucketSocialized()
                     .type(ApiBucketType.LOCATION)
                     .status(BucketStatus.NEW)
                     .name("test")
                     .id(popularId)
                     .build()
               setup()

               bucketInteractor.bucketListActionPipe()
                     .observe()
                     .subscribe(testListSubscriber)

               assertActionSuccess(subscribeAddBucketItem(ImmutableBucketPostBody.builder()
                     .type(type)
                     .id("$popularId")
                     .status(NEW)
                     .build())) {
                  val item = it.result
                  type == item.type && NEW == item.status
               }
               assertBucketWasAddedInList(testListSubscriber)
            }
         }

         context("create bucket item from trip") {

            it("should create item from trip and should add created item into result list") {
               val testName = "Test from trip"
               val tripId = 333

               val mockedTripModel = mock<TripModel>()

               val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

               whenever(testBucketItem.name).thenReturn(testName)
               whenever(mockedTripModel.name).thenReturn(testName)
               testBucketItemApi = getStubbedApiBucketSocialized()
                     .name(testName)
                     .id(tripId)
                     .type(ApiBucketType.LOCATION)
                     .status(BucketStatus.NEW)
                     .build()
               setup()

               bucketInteractor.bucketListActionPipe()
                     .observe()
                     .subscribe(testListSubscriber)

               assertActionSuccess(subscribeAddBucketItem(ImmutableBucketBodyImpl.builder()
                     .type("trip")
                     .id("$tripId")
                     .build())) {
                  mockedTripModel.name == it.result.name
               }

               assertBucketWasAddedInList(testListSubscriber)
            }
         }
      }

      context("item modification") {

         it("should update item and modify updated item into result list") {
               val testSubscriber = TestSubscriber<ActionState<UpdateBucketItemCommand>>()
               val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

               whenever(testBucketItem.uid).thenReturn(TEST_BUCKET_ITEM_UID)
               whenever(testBucketItem.status).thenReturn(COMPLETED)
               testBucketItemSocializedApi = getStubApiBucketSocialized(Integer.parseInt(TEST_BUCKET_ITEM_UID))
                     .status(BucketStatus.COMPLETED)
                     .build()
               setup()

               bucketInteractor.bucketListActionPipe()
                     .observe()
                     .subscribe(testListSubscriber)

               bucketInteractor.updatePipe()
                     .createObservable(UpdateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                           .id(TEST_BUCKET_ITEM_UID)
                           .status(COMPLETED)
                           .build()))
                     .subscribe(testSubscriber)

               assertActionSuccess(testSubscriber) {
                  val responseBucketItem = it.result
                  TEST_BUCKET_ITEM_UID == responseBucketItem.uid
                        && COMPLETED == responseBucketItem.status
               }
               assertActionSuccess(testListSubscriber) {
                  it.result.any({ TEST_BUCKET_ITEM_UID == it.uid && COMPLETED == it.status })
               }
            }
         }

         context("item add photo") {

            it("should create item's photo") {
               val testSubscriber = TestSubscriber<ActionState<AddBucketItemPhotoCommand>>()
               val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
               val testUploadSubscriber = TestSubscriber<ActionState<UploadPhotoControllerCommand>>()

               whenever(testBucketItem.photos)
                     .thenReturn(mutableListOf(testBucketPhoto))

               setup()

               bucketInteractor.bucketListActionPipe()
                     .observe()
                     .subscribe(testListSubscriber)
               bucketInteractor.uploadControllerCommandPipe()
                     .observe()
                     .subscribe(testUploadSubscriber)

               bucketInteractor.addBucketItemPhotoPipe()
                     .createObservable(AddBucketItemPhotoCommand(testBucketItem, TEST_IMAGE_PATH))
                     .subscribe(testSubscriber)

               assertActionSuccess(testSubscriber) {
                  val resultPair = it.result
                  comparePhotos(resultPair.second, testBucketPhotoApi)
                  comparePhotos(resultPair.first.getPhotos()[0], testBucketPhotoApi)
               }

               assertStatusCount(testUploadSubscriber, ActionState.Status.SUCCESS, 2)

               assertActionSuccess(testListSubscriber) {
                  it.result.flatMap { it.photos }.any { it == testBucketPhoto }
               }
            }
         }

         context("item delete photo") {

            it("should delete item photo") {
               val testSubscriber = TestSubscriber<ActionState<DeleteItemPhotoCommand>>()
               val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

               whenever(testBucketItem.photos)
                     .thenReturn(Lists.newArrayList<BucketPhoto>(testBucketPhoto))

               bucketInteractor.bucketListActionPipe()
                     .observe()
                     .subscribe(testListSubscriber)

               bucketInteractor.deleteItemPhotoPipe()
                     .createObservable(DeleteItemPhotoCommand(testBucketItem, testBucketPhoto))
                     .subscribe(testSubscriber)

               assertActionSuccess(testSubscriber) {
                  it.result.photos.none { it == testBucketPhoto }
               }
               assertActionSuccess(testListSubscriber) {
                  it.result.flatMap { it.photos }.none { it == testBucketPhoto }
               }
            }
         }

      context("item delete") {

         it("should delete item and remove from result list") {
            val testSubscriber = TestSubscriber<ActionState<DeleteBucketItemCommand>>()
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            bucketInteractor.bucketListActionPipe()
                  .observe()
                  .subscribe(testListSubscriber)

            val bucketItem = BucketItem()
            bucketItem.uid = TEST_BUCKET_ITEM_UID;
            bucketInteractor.deleteItemPipe()
                  .createObservable(DeleteBucketItemCommand(bucketItem))
                  .subscribe(testSubscriber)
            assertActionSuccess(testSubscriber) { true }
            assertActionSuccess(testListSubscriber) {
               it.result.none { TEST_BUCKET_ITEM_UID == it.uid }
            }
         }
      }

      context("uploading photo state change process") {
         val photoStateHolderProgress = createPhotoEntityHolderWithBehavior(State.PROGRESS)
         val photoStateHolderDone = createPhotoEntityHolderWithBehavior(State.DONE)
         val photoStateHolderFailed = createPhotoEntityHolderWithBehavior(State.FAIL)

         whenever(uploadControllerStorage.get(any()))
               .thenReturn(mutableListOf(photoStateHolderFailed, photoStateHolderDone))

         it("should add photo in progress state to storage") {
            val testSubscriber = subscribeCreateUploadController(photoStateHolderProgress)

            assertActionSuccess(testSubscriber) {
               checkUploadControllerResult(it, photoStateHolderProgress, true)
            }
         }

         it("should change state as fail previously added photo if fail") {
            val testSubscriber = subscribeCreateUploadController(photoStateHolderFailed)

            assertActionSuccess(testSubscriber) {
               checkUploadControllerResult(it, photoStateHolderFailed, true)
            }
         }

         it("should remove previously added photo if success") {
            val testSubscriber = subscribeCreateUploadController(photoStateHolderDone)

            assertActionSuccess(testSubscriber) {
               checkUploadControllerResult(it, photoStateHolderDone, false)
            }
         }
      }

      it("should find item that contains input bucket photo") {
         whenever(testBucketItem.photos)
               .thenReturn(listOf(testBucketPhoto))

         val testSubscriber = TestSubscriber<ActionState<FindBucketItemByPhotoCommand>>()

         bucketInteractor.bucketListActionPipe()
               .createObservable(BucketListCommand.fetch(false))
               .subscribe()
         bucketInteractor.findBucketItemByPhotoActionPipe()
               .createObservable(FindBucketItemByPhotoCommand(testBucketPhoto))
               .subscribe(testSubscriber)
         assertActionSuccess(testSubscriber) {
            it.result != null
         }
      }
   }
}) {
   companion object {
      private val TEST_BUCKET_ITEM_UID = "33"

      private val TEST_BACKEND_PATH = "http://test-server"

      private val TEST_BUCKET_ID = 1
      private val TEST_BUCKET_ID_SECOND = 2

      private val TEST_BUCKET_PHOTO_UID = "1"

      val testBucketItem: BucketItem = getStubBucketItem(TEST_BUCKET_ID)
      val testBucketItem2: BucketItem = getStubBucketItem(TEST_BUCKET_ID_SECOND)
      var testBucketItemApi: BucketItemSocialized = getStubApiBucketSocialized(TEST_BUCKET_ID).build()
      var testBucketItemSocializedApi: BucketItemSocialized = getStubApiBucketSocialized(TEST_BUCKET_ID).build()
      val testBucketPhoto: BucketPhoto = makeStubBucketPhoto(TEST_BUCKET_PHOTO_UID)
      val testBucketPhotoApi: ApiBucketPhoto = makeStubApiBucketPhoto(TEST_BUCKET_PHOTO_UID)
      val testPhotoUploadResponse: UploaderyImageResponse = mock()
      val testUploaderyPhoto: UploaderyImage = mock()

      val mockMemoryStorage: BucketMemoryStorage = spy()
      val mockDb: SnappyRepository = spy()

      val uploadControllerStorage: UploadBucketPhotoInMemoryStorage = mock()

      fun setup() {
         setup(setOfStorage, mockDb) { mockHttpService() }
      }

      val setOfStorage: () -> Set<ActionStorage<*>> = {
         setOf(BucketListDiskStorage(mockMemoryStorage, mockDb), uploadControllerStorage)
      }

      val assertBucketWasAddedInList: (TestSubscriber<ActionState<BucketListCommand>>) -> Unit = {
         assertActionSuccess(it, { it.result.contains(testBucketItem) })
      }

      val subscribeCreateUploadController: (EntityStateHolder<BucketPhoto>) -> TestSubscriber<ActionState<UploadPhotoControllerCommand>> = {
         val testSubscriber = TestSubscriber<ActionState<UploadPhotoControllerCommand>>()

         bucketInteractor.uploadControllerCommandPipe()
               .createObservable(UploadPhotoControllerCommand.create(TEST_BUCKET_ITEM_UID, it))
               .subscribe(testSubscriber)
         testSubscriber
      }

      init {
         whenever(uploadControllerStorage.actionClass).thenCallRealMethod()
         whenever(testPhotoUploadResponse.uploaderyPhoto()).thenReturn(testUploaderyPhoto)
         whenever(testUploaderyPhoto.location()).thenReturn(TEST_BACKEND_PATH)
      }

      fun subscribeAddBucketItem(body: BucketBody): TestSubscriber<ActionState<CreateBucketItemCommand>> {
         val testSubscriber = TestSubscriber<ActionState<CreateBucketItemCommand>>()

         bucketInteractor.createPipe()
               .createObservable(CreateBucketItemCommand(body))
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(testBucketItemApi)) {
                  Method.POST.name == it.method && it.url.endsWith("/api/bucket_list_items")
               }
               .bind(MockHttpActionService.Response(200).body(testBucketItemSocializedApi)) {
                  it.method == Method.PATCH.name
               }
               .bind(MockHttpActionService.Response(200).body(JsonObject())) {
                  Method.DELETE.name == it.method
               }
               .bind(MockHttpActionService.Response(200).body(testBucketItemSocializedApi)) {
                  it.body.toString().contains("id")
                        && Method.PATCH.name == it.method
               }
               .bind(MockHttpActionService.Response(200).body(testBucketPhotoApi)) {
                  it.url.contains("/photos")
               }
               .bind(MockHttpActionService.Response(200).body(testPhotoUploadResponse)) {
                  it.url.contains("/upload")
               }
               .build()
      }

      fun createPhotoEntityHolderWithBehavior(state: State): EntityStateHolder<BucketPhoto> {
         return create(testBucketPhoto, state)
      }

      fun checkUploadControllerResult(command: UploadPhotoControllerCommand,
                                      photoEntityStateHolder: EntityStateHolder<BucketPhoto>,
                                      contains: Boolean): Boolean {
         val hasItem = command.result.contains(photoEntityStateHolder)
         return when (contains) {
            true -> hasItem
            else -> !hasItem
         }
      }

      fun makeStubBucketPhoto(uid: String): BucketPhoto {
         val bucketPhoto = BucketPhoto()
         bucketPhoto.uid = uid
         return bucketPhoto;
      }

      fun makeStubApiBucketPhoto(uid: String): ApiBucketPhoto {
         return ImmutableBucketPhoto.builder()
               .id(Integer.parseInt(uid))
               .uid("$uid")
               .url("http://$uid.jpg")
               .originUrl("http://$uid.jpg")
               .build()
      }

      fun comparePhotos(bucketPhoto: BucketPhoto,
                        apiBucketPhoto: ApiBucketPhoto): Boolean {
         Assert.assertTrue(bucketPhoto.uid.equals(apiBucketPhoto.uid()))
         return true;
      }

      fun getStubBucketItem(id: Int): BucketItem {
         val bucketItem: BucketItem = mock()
         whenever(bucketItem.uid).thenReturn("$id")
         whenever(bucketItem.name).thenReturn("$id")
         return bucketItem
      }

      fun getStubbedApiBucketSocialized(): ImmutableBucketItemSocialized.Builder {
         return getStubApiBucketSocialized(TEST_BUCKET_ID)
      }

      fun getStubApiBucketSocialized(id: Int): ImmutableBucketItemSocialized.Builder {
         return ImmutableBucketItemSocialized.builder()
               .id(id)
               .uid("$id")
               .creationDate(Date())
               .link("")
               .name("$id")
               .type(ApiBucketType.ACTIVITY)
               .status(BucketStatus.NEW)
               .bucketPhoto(emptyList())
               .tags(emptyList())
               .friends(emptyList())
               .liked(false)
               .likes(0)
               .commentsCount(0)
               .author(ImmutableShortUserProfile.builder()
                     .id(id)
                     .firstName("Joe")
                     .lastName("Smith")
                     .username("username")
                     .avatar(ImmutableAvatar.builder().thumb("").medium("").original("").build())
                     .badges(emptyList()).build())
      }
   }
}
