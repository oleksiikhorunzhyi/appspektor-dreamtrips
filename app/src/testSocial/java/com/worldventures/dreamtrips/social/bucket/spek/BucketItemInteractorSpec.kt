package com.worldventures.dreamtrips.social.bucket.spek

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.AssertUtil.assertStatusCount
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.utils.FileUtils
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.*
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.modules.bucketlist.model.PhotoUploadResponse
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.action.MarkItemAsDoneHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateItemHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.command.*
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder.State
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder.create
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketPostBody
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.AppConfig
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager
import io.techery.janet.ActionState
import io.techery.janet.http.annotations.HttpAction.Method
import io.techery.janet.http.test.MockHttpActionService
import org.assertj.core.util.Lists
import org.junit.rules.TemporaryFolder
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import rx.observers.TestSubscriber

@PrepareForTest(UploadingFileManager::class, FileUtils::class, EntityStateHolder::class)
class BucketItemInteractorSpec : BucketInteractorBaseSpec({
    describe("bucket actions on item") {
        setup(setOfStorage) { mockHttpService() }

        beforeEach {
            doReturn(mutableListOf(testBucketItem, mock<BucketItem>()))
                    .whenever(mockMemoryStorage).get(any())
        }

        context("item creation") {
            on("create bucket item") {
                val title = "Test"
                val type = BucketType.LOCATION.name
                val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

                beforeEach {
                    whenever(testBucketItem.name).thenReturn(title)
                    whenever(testBucketItem.type).thenReturn(type)
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
                        val item = it.response
                        item.name == title && item.type == type
                    }
                }

                it("should add created item into result list") {
                    assertBucketWasAddedInList(testListSubscriber)
                }
            }

            on("create bucket item from popular") {
                val type = BucketType.LOCATION.getName()
                val popularId = "testPopularId"
                val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

                beforeEach {
                    whenever(testBucketItem.type).thenReturn(type)
                    whenever(testBucketItem.status).thenReturn(NEW)
                }

                it("should create item from popular") {
                    bucketInteractor.bucketListActionPipe()
                            .observe()
                            .subscribe(testListSubscriber)

                    assertActionSuccess(subscribeAddBucketItem(ImmutableBucketPostBody.builder()
                            .type(type)
                            .id(popularId)
                            .status(NEW)
                            .build())) {
                        val item = it.response
                        type == item.type && NEW == item.status
                    }
                }

                it("should add created item into result list") {
                    assertBucketWasAddedInList(testListSubscriber)
                }
            }

            on("create bucket item from trip") {
                val testName = "Test from trip"
                val tripId = "testTripId"

                val mockedTripModel = mock<TripModel>()

                val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

                beforeEach {
                    whenever(testBucketItem.name).thenReturn(testName)
                    whenever(mockedTripModel.name).thenReturn(testName)
                }

                it("should create item from trip") {
                    bucketInteractor.bucketListActionPipe()
                            .observe()
                            .subscribe(testListSubscriber)

                    assertActionSuccess(subscribeAddBucketItem(ImmutableBucketBodyImpl.builder()
                            .type("trip")
                            .id(tripId)
                            .build())) {
                        mockedTripModel.name == it.response.name
                    }
                }

                it("should add created item into result list") {
                    assertBucketWasAddedInList(testListSubscriber)
                }
            }
        }

        context("item modification") {
            on("item update") {
                val testSubscriber = TestSubscriber<ActionState<UpdateItemHttpAction>>()
                val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

                beforeEach {
                    whenever(testBucketItem.uid).thenReturn(TEST_BUCKET_ITEM_UID)
                    whenever(testBucketItem.status).thenReturn(COMPLETED)
                }

                it("should update item") {
                    bucketInteractor.bucketListActionPipe()
                            .observe()
                            .subscribe(testListSubscriber)

                    bucketInteractor.updatePipe()
                            .createObservable(UpdateItemHttpAction(ImmutableBucketBodyImpl.builder()
                                    .id(TEST_BUCKET_ITEM_UID)
                                    .status(COMPLETED)
                                    .build()))
                            .subscribe(testSubscriber)

                    assertActionSuccess(testSubscriber) {
                        val responseBucketItem = it.response
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

            on("item mark as done") {
                val testSubscriber = TestSubscriber<ActionState<MarkItemAsDoneHttpAction>>()
                val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

                it("should mark item as done") {
                    bucketInteractor.bucketListActionPipe()
                            .observe()
                            .subscribe(testListSubscriber)

                    bucketInteractor.marksAsDonePipe()
                            .createObservable(MarkItemAsDoneHttpAction(TEST_BUCKET_ITEM_UID, COMPLETED))
                            .subscribe(testSubscriber)
                    assertActionSuccess(testSubscriber) {
                        it.response.status == COMPLETED
                    }
                }

                it("should modify updated item into result list") {
                    assertActionSuccess(testListSubscriber) {
                        it.result.any { TEST_BUCKET_ITEM_UID == it.uid && COMPLETED == it.status }
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
                        resultPair.second == testBucketPhoto && resultPair.first.photos.contains(testBucketPhoto)
                    }
                }

                it("should emmit states of uploading") {
                    assertStatusCount(testUploadSubscriber, ActionState.Status.SUCCESS, 2)
                }

                it("should contains in the result list") {
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
            val testSubscriber = TestSubscriber<ActionState<DeleteItemHttpAction>>()
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            it("should delete item") {
                bucketInteractor.bucketListActionPipe()
                        .observe()
                        .subscribe(testListSubscriber)

                bucketInteractor.deleteItemPipe()
                        .createObservable(DeleteItemHttpAction(TEST_BUCKET_ITEM_UID))
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
        private val TEST_BUCKET_ITEM_UID = "test"

        private val TEST_BACKEND_PATH = "http://test-server"

        val testBucketItem: BucketItem = mock()
        val testBucketPhoto: BucketPhoto = mock()
        val testPhotoUploadResponse: PhotoUploadResponse = mock()

        val uploadControllerStorage: UploadBucketPhotoInMemoryStorage = mock()

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
            val mockConfig: AppConfig.URLS.Config = mock()
            val mockAppConfig: AppConfig = mock()
            val mockUrls: AppConfig.URLS = mock()

            whenever(uploadControllerStorage.actionClass).thenCallRealMethod()
            whenever(testPhotoUploadResponse.location).thenReturn(TEST_BACKEND_PATH)


            whenever(mockConfig.uploaderyBaseURL).thenReturn("http://test-uploadery")
            whenever(mockUrls.production).thenReturn(mockConfig)
            whenever(mockAppConfig.urls).thenReturn(mockUrls)
            whenever(userSession.globalConfig).thenReturn(mockAppConfig)
        }

        fun subscribeAddBucketItem(body: BucketBody): TestSubscriber<ActionState<CreateBucketItemHttpAction>> {
            val testSubscriber = TestSubscriber<ActionState<CreateBucketItemHttpAction>>()

            bucketInteractor.createPipe()
                    .createObservable(CreateBucketItemHttpAction(body))
                    .subscribe(testSubscriber)
            return testSubscriber
        }

        fun mockHttpService(): MockHttpActionService {
            return MockHttpActionService.Builder()
                    .bind(MockHttpActionService.Response(200).body(testBucketItem)) {
                        Method.POST.name == it.method && it.url.endsWith("/api/bucket_list_items")
                    }
                    .bind(MockHttpActionService.Response(200).body(testBucketItem)) {
                        it.method == Method.PATCH.name
                    }
                    .bind(MockHttpActionService.Response(200).body(JsonObject())) {
                        Method.DELETE.name == it.method
                    }
                    .bind(MockHttpActionService.Response(200).body(testBucketItem)) {
                        it.body.toString().contains("id")
                                && Method.PATCH.name == it.method
                    }
                    .bind(MockHttpActionService.Response(200).body(testBucketPhoto)) {
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
    }
}
