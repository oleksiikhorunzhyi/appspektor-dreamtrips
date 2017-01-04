package com.worldventures.dreamtrips.social.bucket

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
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
import com.worldventures.dreamtrips.core.utils.FileUtils
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.*
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemCommand
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.modules.bucketlist.service.command.*
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketPostBody
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder.State
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder.create
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager
import io.techery.janet.ActionState
import io.techery.janet.http.annotations.HttpAction.Method
import io.techery.janet.http.test.MockHttpActionService
import junit.framework.Assert
import org.assertj.core.util.Lists
import org.junit.rules.TemporaryFolder
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import rx.observers.TestSubscriber
import java.util.*

@PrepareForTest(UploadingFileManager::class, FileUtils::class, EntityStateHolder::class)
class BucketItemInteractorSpec : BucketInteractorBaseSpec({
   describe("bucket actions on item") {
      setup()

      beforeEach {
         doReturn(mutableListOf(testBucketItem, testBucketItem2))
               .whenever(mockMemoryStorage).get(any())
      }

      context("item creation") {
         on("create bucket item") {
            val title = "Test"
            val type = BucketType.LOCATION.getName()
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            beforeEach {
               whenever(testBucketItem.name).thenReturn(title)
               whenever(testBucketItem.type).thenReturn(type)
               testBucketItemApi = getStubbedApiBucketSocialized()
                     .type(com.worldventures.dreamtrips.api.bucketlist.model.BucketType.LOCATION)
                     .name(title)
                     .status(BucketStatus.NEW)
                     .build()
               setup()
            }

            it("should create item") {
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
            }

            it("should add created item into result list") {
               assertBucketWasAddedInList(testListSubscriber)
            }
         }

         on("create bucket item from popular") {
            val type = BucketType.LOCATION.getName()
            val popularId = 123
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            beforeEach {
               whenever(testBucketItem.type).thenReturn(type)
               whenever(testBucketItem.status).thenReturn(NEW)
               testBucketItemApi = getStubbedApiBucketSocialized()
                     .type(com.worldventures.dreamtrips.api.bucketlist.model.BucketType.LOCATION)
                     .status(BucketStatus.NEW)
                     .name("test")
                     .id(popularId)
                     .build()
               setup()
            }

            it("should create item from popular") {
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
            }

            it("should add created item into result list") {
               assertBucketWasAddedInList(testListSubscriber)
            }
         }

         on("create bucket item from trip") {
            val testName = "Test from trip"
            val tripId = 333

            val mockedTripModel = mock<TripModel>()

            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            beforeEach {
               whenever(testBucketItem.name).thenReturn(testName)
               whenever(mockedTripModel.name).thenReturn(testName)
               testBucketItemApi = getStubbedApiBucketSocialized()
                     .name(testName)
                     .id(tripId)
                     .type(com.worldventures.dreamtrips.api.bucketlist.model.BucketType.LOCATION)
                     .status(BucketStatus.NEW)
                     .build()
               setup()
            }

            it("should create item from trip") {
               bucketInteractor.bucketListActionPipe()
                     .observe()
                     .subscribe(testListSubscriber)

               assertActionSuccess(subscribeAddBucketItem(ImmutableBucketBodyImpl.builder()
                     .type("trip")
                     .id("$tripId")
                     .build())) {
                  mockedTripModel.name == it.result.name
               }
            }

            it("should add created item into result list") {
               assertBucketWasAddedInList(testListSubscriber)
            }
         }
      }

      context("item modification") {
         on("item update") {
            val testSubscriber = TestSubscriber<ActionState<UpdateBucketItemCommand>>()
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            beforeEach {
               whenever(testBucketItem.uid).thenReturn(TEST_BUCKET_ITEM_UID)
               whenever(testBucketItem.status).thenReturn(COMPLETED)
               testBucketItemSocializedApi = getStubApiBucketSocialized(Integer.parseInt(TEST_BUCKET_ITEM_UID))
                     .status(BucketStatus.COMPLETED)
                     .build()
               setup()
            }

            it("should update item") {
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
            }

            it("should modify updated item into result list") {
               assertActionSuccess(testListSubscriber) {
                  it.result.any({ TEST_BUCKET_ITEM_UID == it.uid && COMPLETED == it.status })
               }
            }
         }

         on("item add photo") {
            val folder = TemporaryFolder()
            val path = folder.createFileAndGetPath("TestPhoto.jpeg")

            val testSubscriber = TestSubscriber<ActionState<AddBucketItemPhotoCommand>>()
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            val testUploadSubscriber = TestSubscriber<ActionState<UploadPhotoControllerCommand>>()

            beforeEach {
               mockStatic(UploadingFileManager::class.java)
               mockStatic(FileUtils::class.java)

               whenever(UploadingFileManager.copyFileIfNeed(anyString(), any()))
                     .thenReturn(path)
               whenever(FileUtils.getPath(any(), any()))
                     .thenReturn(path)
            }

            it("should create item's photo") {
               whenever(testBucketItem.photos)
                     .thenReturn(mutableListOf(testBucketPhoto))

               bucketInteractor.bucketListActionPipe()
                     .observe()
                     .subscribe(testListSubscriber)
               bucketInteractor.uploadControllerCommandPipe()
                     .observe()
                     .subscribe(testUploadSubscriber)

               bucketInteractor.addBucketItemPhotoPipe()
                     .createObservable(AddBucketItemPhotoCommand(testBucketItem, path))
                     .subscribe(testSubscriber)

               assertActionSuccess(testSubscriber) {
                  val resultPair = it.result
                  comparePhotos(resultPair.second, testBucketPhotoApi)
                  comparePhotos(resultPair.first.getPhotos()[0], testBucketPhotoApi)
               }
            }

            it("should emmit states of uploading") {
               assertStatusCount(testUploadSubscriber, ActionState.Status.SUCCESS, 2)
            }

            it("should be present in the result list") {
               assertActionSuccess(testListSubscriber) {
                  it.result.flatMap { it.photos }.any { it == testBucketPhoto }
               }
            }
         }

         on("item delete photo") {
            val testSubscriber = TestSubscriber<ActionState<DeleteItemPhotoCommand>>()
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            it("should delete item photo") {
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
            }

            it("should modify updated item into result list") {
               assertActionSuccess(testListSubscriber) {
                  it.result.flatMap { it.photos }.none { it == testBucketPhoto }
               }
            }
         }
      }

      on("item delete") {
         val testSubscriber = TestSubscriber<ActionState<DeleteBucketItemCommand>>()
         val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

         it("should delete item") {
            bucketInteractor.bucketListActionPipe()
                  .observe()
                  .subscribe(testListSubscriber)

            bucketInteractor.deleteItemPipe()
                  .createObservable(DeleteBucketItemCommand(TEST_BUCKET_ITEM_UID))
                  .subscribe(testSubscriber)
            assertActionSuccess(testSubscriber) {
               true
            }
         }

         it("should remove deleted item from result list") {
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
      val testBucketPhotoApi: com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto = makeStubApiBucketPhoto(TEST_BUCKET_PHOTO_UID)
      val testPhotoUploadResponse: UploaderyImageResponse = mock()
      val testUploaderyPhoto: UploaderyImage = mock()

      val uploadControllerStorage: UploadBucketPhotoInMemoryStorage = mock()

      fun setup() {
         setup(setOfStorage) { mockHttpService() }
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

      fun TemporaryFolder.createFileAndGetPath(fileName: String): String {
         this.create()
         return this.newFile(fileName).path
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

      fun makeStubApiBucketPhoto(uid: String): com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto {
         return ImmutableBucketPhoto.builder()
               .id(Integer.parseInt(uid))
               .uid("$uid")
               .url("http://$uid.jpg")
               .originUrl("http://$uid.jpg")
               .build()
      }

      fun comparePhotos(bucketPhoto: BucketPhoto,
                        apiBucketPhoto: com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto): Boolean {
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
               .type(com.worldventures.dreamtrips.api.bucketlist.model.BucketType.ACTIVITY)
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
