package com.worldventures.dreamtrips.modules.social.bucket

import android.content.Context
import android.test.mock.MockContext
import android.text.TextUtils
import com.google.gson.JsonObject
import com.innahema.collections.query.queriables.Queryable
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.test.AssertUtil.*
import com.worldventures.dreamtrips.core.utils.FileUtils
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.COMPLETED
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.NEW
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.modules.bucketlist.model.PhotoUploadResponse
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.action.MarkItemAsDoneHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateItemHttpAction
import com.worldventures.dreamtrips.modules.bucketlist.service.command.*
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketPostBody
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage
import com.worldventures.dreamtrips.modules.common.model.AppConfig
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager
import io.techery.janet.ActionState
import io.techery.janet.http.annotations.HttpAction
import io.techery.janet.http.test.MockHttpActionService
import org.assertj.core.util.Lists
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Matchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import rx.functions.Func1
import rx.observers.TestSubscriber
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit

@RunWith(PowerMockRunner::class)
@PrepareForTest(UploadingFileManager::class, FileUtils::class, EntityStateHolder::class)
class BucketItemInteractorTest : BucketInteractorBaseTest() {

    @Rule
    val folder = TemporaryFolder()

    @Mock
    internal var uploadControllerStorage: UploadBucketPhotoInMemoryStorage? = null

    private var testBucketItem: BucketItem? = null

    private var testPhotoUploadResponse: PhotoUploadResponse? = null
    private var testBucketPhoto: BucketPhoto? = null

    init {
        testBucketItem = mock(BucketItem::class.java)
        testBucketPhoto = mock(BucketPhoto::class.java)
        testPhotoUploadResponse = mock(PhotoUploadResponse::class.java)

        `when`(testPhotoUploadResponse!!.location).thenReturn(TEST_BACKEND_PATH)

        `when`(testBucketItem!!.uid).thenReturn(TEST_BUCKET_ITEM_UID)
        `when`(testBucketItem!!.status).thenReturn(COMPLETED)

        `when`(testBucketPhoto!!.fsId).thenReturn("test")
        `when`(testBucketItem!!.photos).thenReturn(Lists.newArrayList<BucketPhoto>(testBucketPhoto))
    }

    @Before
    override fun setup() {
        super.setup()

        val testPhotoPath = createFileAndGetPath()

        mockStatic(UploadingFileManager::class.java)
        mockStatic(FileUtils::class.java)

        try {
            PowerMockito.`when`(UploadingFileManager.copyFileIfNeed(anyString(), any()))
                    .thenReturn(testPhotoPath)
            PowerMockito.`when`<String>(FileUtils.getPath(any(), any()))
                    .thenReturn(testPhotoPath)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        daggerActionService.registerProvider(UploaderyManager::class.java) { UploaderyManager(janet) }
        daggerActionService.registerProvider(Context::class.java, { MockContext() })

        `when`(mockMemoryStorage!!.get(null))
                .thenReturn(Lists.newArrayList<BucketItem>(testBucketItem, mock(BucketItem::class.java)))

        //session
        val mockConfig = mock(AppConfig.URLS.Config::class.java)
        val mockAppConfig = mock(AppConfig::class.java)
        val mockUrls = mock(AppConfig.URLS::class.java)

        `when`(mockConfig.uploaderyBaseURL).thenReturn("http://test-uploadery")
        `when`(mockUrls.production).thenReturn(mockConfig)
        `when`(mockAppConfig.urls).thenReturn(mockUrls)
        `when`(userSession!!.globalConfig).thenReturn(mockAppConfig)
    }

    @Test
    fun createItemTest() {
        val title = "Test"
        val type = BucketItem.BucketType.LOCATION
        `when`(testBucketItem!!.name).thenReturn(title)
        `when`(testBucketItem!!.type).thenReturn(type.getName())

        checkBucketItemCreation(ImmutableBucketPostBody.builder()
                .type(type.getName())
                .name(title)
                .status(NEW)
                .build(), Func1 { createBucketItemAction ->
            val item = createBucketItemAction.response
            item.name == title && item.type == type.getName()
        })
    }

    @Test
    fun createItemFromPopularTest() {
        val testName = "Test from trip"
        val testTripModel = mock(TripModel::class.java)

        `when`(testTripModel.name).thenReturn(testName)
        `when`(testBucketItem!!.name).thenReturn(testName)

        checkBucketItemCreation(ImmutableBucketBodyImpl.builder()
                .type("trip")
                .id(TEST_BUCKET_ITEM_UID)
                .build(), Func1 { createBucketItemAction -> testTripModel.name == createBucketItemAction.response.name })
    }

    @Test
    fun createItemFromTripTest() {
        val type = BucketItem.BucketType.LOCATION.getName()
        `when`(testBucketItem!!.type).thenReturn(type)
        `when`(testBucketItem!!.status).thenReturn(NEW)

        checkBucketItemCreation(ImmutableBucketPostBody.builder()
                .type(type)
                .id(TEST_BUCKET_ITEM_UID)
                .status(NEW)
                .build(), Func1 { createBucketItemAction ->
            val item = createBucketItemAction.response
            type == item.type && NEW == item.status
        })
    }

    @Test
    fun updateItemStatusTest() {
        checkBucketItemUpdate(ImmutableBucketBodyImpl.builder()
                .id(TEST_BUCKET_ITEM_UID)
                .status(COMPLETED)
                .build(), Func1 { item -> TEST_BUCKET_ITEM_UID == item.uid && COMPLETED == item.status })
    }

    @Test
    fun addBucketItemPhotoTest() {
        val testListSubscriber = subscribeBucketListChanges()
        val testSubscriber = TestSubscriber<ActionState<AddBucketItemPhotoCommand>>()

        bucketInteractor!!.addBucketItemPhotoPipe()
                .createObservable(AddBucketItemPhotoCommand(testBucketItem, null))
                .subscribe(testSubscriber)

        assertActionSuccess(testSubscriber) { addBucketItemPhotoCommand ->
            val resultPair = addBucketItemPhotoCommand.result
            resultPair.second == testBucketPhoto && resultPair.first.photos.contains(testBucketPhoto)

        }
        assertActionSuccess(testListSubscriber) { bucketListAction -> bucketListAction.result.any { element -> element.photos.contains(testBucketPhoto) } }
    }

    @Test
    fun cancelAddBucketItemPhotoTest() {
        val testListSubscriber = subscribeBucketListChanges()
        val testSubscriber = TestSubscriber<ActionState<AddBucketItemPhotoCommand>>()

        val addBucketPhotoPipe = bucketInteractor!!.addBucketItemPhotoPipe()
        val addBucketItemPhotoCommand = AddBucketItemPhotoCommand(testBucketItem, null)

        addBucketPhotoPipe.createObservable(addBucketItemPhotoCommand)
                .delay(100L, TimeUnit.MILLISECONDS)
                .subscribe(testSubscriber)
        addBucketPhotoPipe.cancel(addBucketItemPhotoCommand)

        assertActionCanceled(testSubscriber)
        assertSubscriberWithoutValues(testListSubscriber)
    }

    @Test
    fun startUploadingPhotoControllerTest() {
        `when`(uploadControllerStorage!!.get(null)).thenReturn(Lists.newArrayList<EntityStateHolder<BucketPhoto>>())

        val testPhotoEntityStateHolder = mockPhotoEntityHolderWithBehavior(EntityStateHolder.State.PROGRESS)
        val testSubscriber = subscribeCreateUploadController(testPhotoEntityStateHolder)

        assertActionSuccess(testSubscriber) { uploadPhotoControllerCommand -> checkUploadingControllerByState(uploadPhotoControllerCommand, testPhotoEntityStateHolder) }
    }

    @Test
    fun failUploadingPhotoControllerTest() {
        val testPhotoEntityStateHolder = mockPhotoEntityHolderWithBehavior(EntityStateHolder.State.FAIL)
        val testSubscriber = subscribeCreateUploadController(testPhotoEntityStateHolder)

        assertActionSuccess(testSubscriber) { uploadPhotoControllerCommand -> checkUploadingControllerByState(uploadPhotoControllerCommand, testPhotoEntityStateHolder) }
    }

    @Test
    fun doneUploadingPhotoControllerTest() {
        val testPhotoEntityStateHolder = mockPhotoEntityHolderWithBehavior(EntityStateHolder.State.DONE)
        `when`(uploadControllerStorage!!.get(any())).thenReturn(Lists.newArrayList(testPhotoEntityStateHolder))

        val testSubscriber = subscribeCreateUploadController(testPhotoEntityStateHolder)

        assertActionSuccess(testSubscriber) { command -> !command.result.contains(testPhotoEntityStateHolder) }
    }

    @Test
    fun deleteItemTest() {
        val testListSubscriber = subscribeBucketListChanges()
        val testSubscriber = TestSubscriber<ActionState<DeleteItemHttpAction>>()

        bucketInteractor!!.deleteItemPipe()
                .createObservable(DeleteItemHttpAction(TEST_BUCKET_ITEM_UID))
                .subscribe(testSubscriber)

        assertActionSuccess(testSubscriber) { deleteItemAction -> true }
        assertActionSuccess(testListSubscriber) { bucketListAction -> !Queryable.from(bucketListAction.result).any { element -> TEST_BUCKET_ITEM_UID == element.uid } }
    }

    @Test
    fun deleteItemPhotoTest() {
        val testListSubscriber = subscribeBucketListChanges()
        val testSubscriber = TestSubscriber<ActionState<DeleteItemPhotoCommand>>()

        bucketInteractor!!.deleteItemPhotoPipe()
                .createObservable(DeleteItemPhotoCommand(testBucketItem, testBucketPhoto))
                .subscribe(testSubscriber)

        assertActionSuccess(testSubscriber) {
            !it.result.photos.contains(testBucketPhoto)
        }
        assertActionSuccess(testListSubscriber) { bucketListAction -> !Queryable.from(bucketListAction.result).any { element -> element.photos.contains(testBucketPhoto) } }
    }

    @Test
    fun markItemAsDoneTest() {
        val testListSubscriber = subscribeBucketListChanges()
        val testSubscriber = TestSubscriber<ActionState<MarkItemAsDoneHttpAction>>()

        bucketInteractor!!.marksAsDonePipe()
                .createObservable(MarkItemAsDoneHttpAction(TEST_BUCKET_ITEM_UID, COMPLETED)).subscribe(testSubscriber)

        assertActionSuccess(testSubscriber) { markItemAsDoneAction -> TextUtils.equals(markItemAsDoneAction.response.status, COMPLETED) }
        assertActionSuccess(testListSubscriber) { bucketListAction -> Queryable.from(bucketListAction.result).any { element -> TEST_BUCKET_ITEM_UID == element.uid && COMPLETED == element.status } }
    }

    @Test
    fun findBucketItemByPhotoTest() {
        `when`(testBucketItem!!.photos).thenReturn(Lists.newArrayList<BucketPhoto>(testBucketPhoto, mock(BucketPhoto::class.java)))

        val testSubscriber = TestSubscriber<ActionState<FindBucketItemByPhotoCommand>>()

        bucketInteractor!!.bucketListActionPipe().createObservable(BucketListCommand.fetch(false)).subscribe()
        bucketInteractor!!.findBucketItemByPhotoActionPipe()
                .createObservable(FindBucketItemByPhotoCommand(testBucketPhoto))
                .subscribe(testSubscriber)

        assertActionSuccess(testSubscriber) { findBucketItemByPhotoAction -> findBucketItemByPhotoAction.result != null }
    }

    override fun mockHttpService(): MockHttpActionService {
        return MockHttpActionService.Builder()
                .bind(MockHttpActionService.Response(200).body(testBucketItem))
                { request -> TextUtils.equals(request.method, HttpAction.Method.POST.name) && request.url.endsWith("/api/bucket_list_items") }
                .bind(MockHttpActionService.Response(200).body(testBucketItem))
                { request -> TextUtils.equals(request.method, HttpAction.Method.PATCH.name) }
                .bind(MockHttpActionService.Response(200).body(JsonObject()))
                { request -> TextUtils.equals(request.method, HttpAction.Method.DELETE.name) }
                .bind(MockHttpActionService.Response(200).body(testBucketItem))
                { request -> request.body.toString().contains("id") && TextUtils.equals(request.method, HttpAction.Method.PATCH.name) }
                .bind(MockHttpActionService.Response(200).body(testBucketPhoto)) { request -> request.url.contains("/photos") }
                .bind(MockHttpActionService.Response(200).body(testPhotoUploadResponse)) { request -> request.url.contains("/upload") }.build()
    }

    override fun storageSet(): Set<ActionStorage<*>> {
        `when`(uploadControllerStorage!!.actionClass).thenCallRealMethod()
        return super.storageSet() + uploadControllerStorage as ActionStorage<*>
    }

    private fun checkBucketItemCreation(body: BucketBody, predicate: Func1<CreateBucketItemHttpAction, Boolean>) {
        val testListSubscriber = subscribeBucketListChanges()
        val testSubscriber = TestSubscriber<ActionState<CreateBucketItemHttpAction>>()

        bucketInteractor!!.createPipe()
                .createObservable(CreateBucketItemHttpAction(body))
                .subscribe(testSubscriber)

        assertActionSuccess(testSubscriber, predicate)
        assertActionSuccess(testListSubscriber) { bucketListAction -> bucketListAction.result.contains(testBucketItem) }
    }

    private fun checkBucketItemUpdate(body: BucketBody, predicate: Func1<BucketItem, Boolean>) {
        val testListSubscriber = subscribeBucketListChanges()
        val testSubscriber = TestSubscriber<ActionState<UpdateItemHttpAction>>()

        bucketInteractor!!.updatePipe()
                .createObservable(UpdateItemHttpAction(body))
                .subscribe(testSubscriber)

        assertActionSuccess(testSubscriber) { updateItemAction -> predicate.call(updateItemAction.response) }
        assertActionSuccess(testListSubscriber) { bucketListAction -> bucketListAction.result.any({ predicate.call(it) }) }
    }

    private fun subscribeBucketListChanges(): TestSubscriber<ActionState<BucketListCommand>> {
        val testListSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

        bucketInteractor!!.bucketListActionPipe()
                .observe()
                .subscribe(testListSubscriber)
        return testListSubscriber
    }

    private fun subscribeCreateUploadController(photoEntityStateHolder: EntityStateHolder<BucketPhoto>): TestSubscriber<ActionState<UploadPhotoControllerCommand>> {
        val testSubscriber = TestSubscriber<ActionState<UploadPhotoControllerCommand>>()

        bucketInteractor!!.uploadControllerCommandPipe()
                .createObservable(UploadPhotoControllerCommand.create(TEST_BUCKET_ITEM_UID, photoEntityStateHolder))
                .subscribe(testSubscriber)
        return testSubscriber
    }

    private fun checkUploadingControllerByState(command: UploadPhotoControllerCommand, photoEntityStateHolder: EntityStateHolder<BucketPhoto>): Boolean {
        return command.result.contains(photoEntityStateHolder)
    }

    private fun createFileAndGetPath(): String? {
        var testPhotoFile: File? = null
        try {
            testPhotoFile = folder.newFile("TestPhoto.jpeg")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return testPhotoFile?.path
    }

    private fun mockPhotoEntityHolderWithBehavior(state: EntityStateHolder.State): EntityStateHolder<BucketPhoto> {
        val testPhotoEntityStateHolder = createMockEntityStateHolder<BucketPhoto>()

        `when`(testPhotoEntityStateHolder.entity()).thenReturn(testBucketPhoto)
        `when`(testPhotoEntityStateHolder.state()).thenReturn(state)

        return testPhotoEntityStateHolder
    }

    @SuppressWarnings("unchecked")
    private fun <T> createMockEntityStateHolder(): EntityStateHolder<T> = mock(EntityStateHolder::class.java) as EntityStateHolder<T>

    companion object {
        private val TEST_BUCKET_ITEM_UID = "test"

        private val TEST_BACKEND_PATH = "http://test-server"
    }
}