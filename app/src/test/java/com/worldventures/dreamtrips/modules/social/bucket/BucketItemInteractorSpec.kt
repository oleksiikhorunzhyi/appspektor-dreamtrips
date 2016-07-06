package com.worldventures.dreamtrips.modules.social.bucket

import android.content.Context
import android.test.mock.MockContext
import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.core.test.StubServiceWrapper
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
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.AppConfig
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.annotations.HttpAction.Method
import io.techery.janet.http.test.MockHttpActionService
import org.assertj.core.util.Lists
import org.junit.Before
import org.junit.rules.TemporaryFolder
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import rx.observers.TestSubscriber

@PrepareForTest(UploadingFileManager::class, FileUtils::class, EntityStateHolder::class)
class BucketItemInteractorSpec : BucketInteractorBaseSpec({
    describe("bucket actions on item") {
        testBucketItem = mock()
        testBucketPhoto = mock()

        httpStubWrapper = StubServiceWrapper(mockHttpService())
        httpStubWrapper.callback = spy()

        daggerCommandActionService = CommandActionService()
                .wrapCache()
                .bindStorageSet(storageSet())
                .wrapDagger()

        daggerCommandActionService.registerProvider(UploaderyManager::class.java) { UploaderyManager(janet) }
        daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })

        setup()

        beforeEach {
            whenever(mockMemoryStorage.get(null))
                    .thenReturn(Lists.newArrayList<BucketItem>(testBucketItem, mock<BucketItem>()))
        }

        context("item creation") {
            on("create bucket item") {
                val title = "Test"
                val type = BucketType.LOCATION.name
                var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

                beforeEach {
                    whenever(testBucketItem.name).thenReturn(title)
                    whenever(testBucketItem.type).thenReturn(type)
                }

                it("should create item") {
                    testListSubscriber = subscribeBucketListChanges()
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
                    assertBucketWasAddedInList(testListSubscriber!!)
                }
            }

            on("create bucket item from popular") {
                val type = BucketType.LOCATION.getName()
                val popularId = "testPopularId"
                var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

                beforeEach {
                    whenever(testBucketItem.type).thenReturn(type)
                    whenever(testBucketItem.status).thenReturn(NEW)
                }

                it("should create item from popular") {
                    testListSubscriber = subscribeBucketListChanges()
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
                    assertBucketWasAddedInList(testListSubscriber!!)
                }
            }

            on("create bucket item from trip") {
                val testName = "Test from trip"
                val tripId = "testTripId"

                val mockedTripModel = mock<TripModel>()

                var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

                beforeEach {
                    whenever(testBucketItem.name).thenReturn(testName)

                    whenever(mockedTripModel.name).thenReturn(testName)
                }

                it("should create item from trip") {
                    testListSubscriber = subscribeBucketListChanges()
                    assertActionSuccess(subscribeAddBucketItem(ImmutableBucketBodyImpl.builder()
                            .type("trip")
                            .id(tripId)
                            .build())) {
                        mockedTripModel.name == it.response.name
                    }
                }

                it("should add created item into result list") {
                    assertBucketWasAddedInList(testListSubscriber!!)
                }
            }
        }

        context("item modification") {
            on("item update") {
                var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

                beforeEach {
                    whenever(testBucketItem.uid).thenReturn(TEST_BUCKET_ITEM_UID)
                    whenever(testBucketItem.status).thenReturn(COMPLETED)
                }

                it("should update item") {
                    val testSubscriber = TestSubscriber<ActionState<UpdateItemHttpAction>>()
                    testListSubscriber = subscribeBucketListChanges()

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
                    assertActionSuccess(testListSubscriber!!) {
                        it.result.any({ TEST_BUCKET_ITEM_UID == it.uid && COMPLETED == it.status })
                    }
                }
            }

            on("item mark as done") {
                var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

                it("should mark item as done") {
                    val testSubscriber = TestSubscriber<ActionState<MarkItemAsDoneHttpAction>>()
                    testListSubscriber = subscribeBucketListChanges()

                    bucketInteractor.marksAsDonePipe()
                            .createObservable(MarkItemAsDoneHttpAction(TEST_BUCKET_ITEM_UID, COMPLETED))
                            .subscribe(testSubscriber)
                    assertActionSuccess(testSubscriber) {
                        it.response.status == COMPLETED
                    }
                }

                it("should modify updated item into result list") {
                    assertActionSuccess(testListSubscriber!!) {
                        it.result.any { TEST_BUCKET_ITEM_UID == it.uid && COMPLETED == it.status }
                    }
                }
            }

            on("item add photo") {
                val folder = TemporaryFolder()
                val path = folder.createFileAndGetPath("TestPhoto.jpeg")

                var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

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
                            .thenReturn(Lists.newArrayList<BucketPhoto>(testBucketPhoto))

                    val testSubscriber = TestSubscriber<ActionState<AddBucketItemPhotoCommand>>()
                    testListSubscriber = subscribeBucketListChanges()

                    bucketInteractor.addBucketItemPhotoPipe()
                            .createObservable(AddBucketItemPhotoCommand(testBucketItem, path))
                            .subscribe(testSubscriber)

                    assertActionSuccess(testSubscriber) {
                        val resultPair = it.result
                        resultPair.second == testBucketPhoto && resultPair.first.photos.contains(testBucketPhoto)
                    }
                }

                it("should contains in the result list") {
                    assertActionSuccess(testListSubscriber) {
                        it.result.any { it.photos.contains(testBucketPhoto) }
                    }
                }
            }

            on("item delete photo") {
                var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

                it("should delete item photo") {
                    whenever(testBucketItem.photos)
                            .thenReturn(Lists.newArrayList<BucketPhoto>(testBucketPhoto))

                    val testSubscriber = TestSubscriber<ActionState<DeleteItemPhotoCommand>>()
                    testListSubscriber = subscribeBucketListChanges()

                    bucketInteractor.deleteItemPhotoPipe()
                            .createObservable(DeleteItemPhotoCommand(testBucketItem, testBucketPhoto))
                            .subscribe(testSubscriber)
                    assertActionSuccess(testSubscriber) {
                        !it.result.getPhotos().contains(testBucketPhoto)
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
            var testListSubscriber: TestSubscriber<ActionState<BucketListCommand>>? = null

            it("should delete item") {
                val testSubscriber = TestSubscriber<ActionState<DeleteItemHttpAction>>()
                testListSubscriber = subscribeBucketListChanges()

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
            val photoStateHolderFailed = createPhotoEntityHolderWithBehavior(State.FAIL)
            val photoStateHolderDone = createPhotoEntityHolderWithBehavior(State.DONE)

            whenever(uploadControllerStorage.get(any()))
                    .thenReturn(mutableListOf(photoStateHolderFailed, photoStateHolderDone))

            it("should add photo in progress state to storage") {
                val photoStateHolderInProgress = createPhotoEntityHolderWithBehavior(State.PROGRESS)
                val testSubscriber = subscribeCreateUploadController(photoStateHolderInProgress)

                assertActionSuccess(testSubscriber) {
                    checkUploadControllerResult(it, photoStateHolderInProgress, true)
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
                    .thenReturn(Lists.newArrayList<BucketPhoto>(testBucketPhoto))

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
    @Before
    fun setUp() {
        mockStatic(UploadingFileManager::class.java)
        mockStatic(FileUtils::class.java)
    }

    companion object {
        private val TEST_BUCKET_ITEM_UID = "test"

        private val TEST_BACKEND_PATH = "http://test-server"

        val uploadControllerStorage: UploadBucketPhotoInMemoryStorage = mock()

        lateinit var testBucketItem: BucketItem

        lateinit var testBucketPhoto: BucketPhoto

        val testPhotoUploadResponse: PhotoUploadResponse = mock()

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
            mockMemoryStorage = spy()
            mockDb = spy()

            //session
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

        fun subscribeBucketListChanges(): TestSubscriber<ActionState<BucketListCommand>> {
            val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            bucketInteractor.bucketListActionPipe()
                    .observe()
                    .subscribe(testListSubscriber)
            return testListSubscriber
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

        fun storageSet(): Set<ActionStorage<*>> {
            return BaseCompanion.storageSet() + uploadControllerStorage as ActionStorage<*>
        }

        fun TemporaryFolder.createFileAndGetPath(fileName: String): String {
            this.create()
            return this.newFile(fileName).path
        }

        fun createPhotoEntityHolderWithBehavior(state: EntityStateHolder.State): EntityStateHolder<BucketPhoto> {
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