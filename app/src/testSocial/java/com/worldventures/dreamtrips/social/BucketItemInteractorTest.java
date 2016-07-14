package com.worldventures.dreamtrips.social;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.test.mock.MockContext;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.test.MockHttpActionService;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.PhotoUploadResponse;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.MarkItemAsDoneHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.UploadPhotoControllerCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketPostBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.techery.janet.ActionState;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.http.annotations.HttpAction;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.core.test.AssertUtil.assertActionCanceled;
import static com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess;
import static com.worldventures.dreamtrips.core.test.AssertUtil.assertSubscriberWithoutValues;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.COMPLETED;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.NEW;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({UploadingFileManager.class, FileUtils.class, EntityStateHolder.class})
public class BucketItemInteractorTest extends BucketInteractorBaseTest {
    private static final String TEST_BUCKET_ITEM_UID = "test";

    private static final String TEST_BACKEND_PATH = "http://test-server";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    UploaderyManager uploaderyManager;

    @Mock
    UploadBucketPhotoInMemoryStorage uploadControllerStorage;

    private BucketItem testBucketItem;

    private PhotoUploadResponse testPhotoUploadResponse;
    private BucketPhoto testBucketPhoto;

    {
        testBucketItem = mock(BucketItem.class);
        testBucketPhoto = mock(BucketPhoto.class);
        testPhotoUploadResponse = mock(PhotoUploadResponse.class);

        when(testPhotoUploadResponse.getLocation())
                .thenReturn(TEST_BACKEND_PATH);

        when(testBucketItem.getUid()).thenReturn(TEST_BUCKET_ITEM_UID);
        when(testBucketItem.getStatus()).thenReturn(COMPLETED);

        when(testBucketPhoto.getFSId()).thenReturn("test");
        when(testBucketItem.getPhotos()).thenReturn(Lists.newArrayList(testBucketPhoto));
    }

    @Before
    public void setup() throws URISyntaxException, IOException {
        super.setup();

        String testPhotoPath = createFileAndGetPath();

        mockStatic(UploadingFileManager.class);
        mockStatic(FileUtils.class);

        PowerMockito.when(UploadingFileManager.copyFileIfNeed(anyString(), any()))
                .thenReturn(testPhotoPath);
        PowerMockito.when(FileUtils.getPath(any(), any()))
                .thenReturn(testPhotoPath);

        daggerActionService().registerProvider(UploaderyManager.class, () -> new UploaderyManager(janet));
        daggerActionService().registerProvider(Context.class, MockContext::new);
        when(mockMemoryStorage.get(null))
                .thenReturn(Lists.newArrayList(testBucketItem, mock(BucketItem.class)));

        //session
        AppConfig.URLS.Config mockConfig = mock(AppConfig.URLS.Config.class);
        AppConfig mockAppConfig = mock(AppConfig.class);
        AppConfig.URLS mockUrls = mock(AppConfig.URLS.class);

        when(mockConfig.getUploaderyBaseURL())
                .thenReturn("http://test-uploadery");
        when(mockUrls.getProduction())
                .thenReturn(mockConfig);
        when(mockAppConfig.getUrls())
                .thenReturn(mockUrls);
        when(userSession.getGlobalConfig())
                .thenReturn(mockAppConfig);
    }

    @Test
    public void createItemTest() {
        String title = "Test";
        BucketItem.BucketType type = BucketItem.BucketType.LOCATION;
        when(testBucketItem.getName()).thenReturn(title);
        when(testBucketItem.getType()).thenReturn(type.getName());

        checkBucketItemCreation(ImmutableBucketPostBody.builder()
                        .type(type.getName())
                        .name(title)
                        .status(NEW)
                        .build(),
                createBucketItemAction -> {
                    BucketItem item = createBucketItemAction.getResponse();
                    return item.getName().equals(title)
                            && item.getType().equals(type.getName());
                });
        verifyBucketListStorage();
    }

    @Test
    public void createItemFromPopularTest() {
        String testName = "Test from trip";
        TripModel testTripModel = mock(TripModel.class);

        when(testTripModel.getName()).thenReturn(testName);
        when(testBucketItem.getName()).thenReturn(testName);

        checkBucketItemCreation(ImmutableBucketBodyImpl.builder()
                        .type("trip")
                        .id(TEST_BUCKET_ITEM_UID)
                        .build(),
                createBucketItemAction -> testTripModel.getName()
                        .equals(createBucketItemAction.getResponse().getName()));
        verifyBucketListStorage();
    }

    @Test
    public void createItemFromTripTest() {
        String type = BucketItem.BucketType.LOCATION.getName();
        when(testBucketItem.getType())
                .thenReturn(type);
        when(testBucketItem.getStatus())
                .thenReturn(NEW);

        checkBucketItemCreation(ImmutableBucketPostBody.builder()
                        .type(type)
                        .id(TEST_BUCKET_ITEM_UID)
                        .status(NEW)
                        .build(),
                createBucketItemAction -> {
                    BucketItem item = createBucketItemAction.getResponse();
                    return type.equals(item.getType()) && NEW.equals(item.getStatus());
                });
        verifyBucketListStorage();
    }

    @Test
    public void updateItemStatusTest() {
        checkBucketItemUpdate(ImmutableBucketBodyImpl.builder()
                        .id(TEST_BUCKET_ITEM_UID)
                        .status(COMPLETED)
                        .build(),
                item -> TEST_BUCKET_ITEM_UID.equals(item.getUid())
                        && COMPLETED.equals(item.getStatus()));
        verifyBucketListStorage();
    }

    @Test
    public void addBucketItemPhotoTest() {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = subscribeBucketListChanges();
        TestSubscriber<ActionState<AddBucketItemPhotoCommand>> testSubscriber = new TestSubscriber<>();

        bucketInteractor.addBucketItemPhotoPipe()
                .createObservable(new AddBucketItemPhotoCommand(testBucketItem, null))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, addBucketItemPhotoCommand -> {
            Pair<BucketItem, BucketPhoto> resultPair = addBucketItemPhotoCommand.getResult();
            return resultPair.second.equals(testBucketPhoto)
                    && resultPair.first.getPhotos().contains(testBucketPhoto);

        });
        assertActionSuccess(testListSubscriber, bucketListAction -> Queryable.from(bucketListAction.getResult())
                .any(element -> element.getPhotos().contains(testBucketPhoto)));
        verifyBucketListStorage();
    }

    @Test
    public void cancelAddBucketItemPhotoTest() {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = subscribeBucketListChanges();
        TestSubscriber<ActionState<AddBucketItemPhotoCommand>> testSubscriber = new TestSubscriber<>();

        WriteActionPipe<AddBucketItemPhotoCommand> addBucketPhotoPipe = bucketInteractor.addBucketItemPhotoPipe();
        AddBucketItemPhotoCommand addBucketItemPhotoCommand = new AddBucketItemPhotoCommand(testBucketItem, null);

        addBucketPhotoPipe
                .createObservable(addBucketItemPhotoCommand)
                .delay(100L, TimeUnit.MILLISECONDS)
                .subscribe(testSubscriber);
        addBucketPhotoPipe.cancel(addBucketItemPhotoCommand);

        assertActionCanceled(testSubscriber);
        assertSubscriberWithoutValues(testListSubscriber);
    }

    @Test
    public void startUploadingPhotoControllerTest() {
        when(uploadControllerStorage.get(null))
                .thenReturn(Lists.newArrayList());

        EntityStateHolder<BucketPhoto> testPhotoEntityStateHolder = mockPhotoEntityHolderWithBehavior(EntityStateHolder.State.PROGRESS);
        TestSubscriber<ActionState<UploadPhotoControllerCommand>> testSubscriber = subscribeCreateUploadController(testPhotoEntityStateHolder);

        assertActionSuccess(testSubscriber, uploadPhotoControllerCommand -> checkUploadingControllerByState(uploadPhotoControllerCommand, testPhotoEntityStateHolder));
        verifyUploadStorage();
    }

    @Test
    public void failUploadingPhotoControllerTest() {
        EntityStateHolder<BucketPhoto> testPhotoEntityStateHolder = mockPhotoEntityHolderWithBehavior(EntityStateHolder.State.FAIL);
        TestSubscriber<ActionState<UploadPhotoControllerCommand>> testSubscriber = subscribeCreateUploadController(testPhotoEntityStateHolder);

        assertActionSuccess(testSubscriber, uploadPhotoControllerCommand -> checkUploadingControllerByState(uploadPhotoControllerCommand, testPhotoEntityStateHolder));
        verifyUploadStorage();
    }

    @Test
    public void doneUploadingPhotoControllerTest() {
        EntityStateHolder<BucketPhoto> testPhotoEntityStateHolder = mockPhotoEntityHolderWithBehavior(EntityStateHolder.State.DONE);
        when(uploadControllerStorage.get(any()))
                .thenReturn(Lists.newArrayList(testPhotoEntityStateHolder));
        TestSubscriber<ActionState<UploadPhotoControllerCommand>> testSubscriber = subscribeCreateUploadController(testPhotoEntityStateHolder);

        assertActionSuccess(testSubscriber, command -> !command.getResult().contains(testPhotoEntityStateHolder));
        verifyUploadStorage();
    }

    @Test
    public void deleteItemTest() {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = subscribeBucketListChanges();
        TestSubscriber<ActionState<DeleteItemHttpAction>> testSubscriber = new TestSubscriber<>();

        bucketInteractor.deleteItemPipe()
                .createObservable(new DeleteItemHttpAction(TEST_BUCKET_ITEM_UID))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, deleteItemAction -> true);
        assertActionSuccess(testListSubscriber, bucketListAction -> !Queryable.from(bucketListAction.getResult())
                .any(element -> TEST_BUCKET_ITEM_UID.equals(element.getUid())));
        verifyBucketListStorage();
    }

    @Test
    public void deleteItemPhotoTest() {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = subscribeBucketListChanges();
        TestSubscriber<ActionState<DeleteItemPhotoCommand>> testSubscriber = new TestSubscriber<>();

        bucketInteractor.deleteItemPhotoPipe()
                .createObservable(new DeleteItemPhotoCommand(testBucketItem, testBucketPhoto))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, deleteItemPhotoAction -> !deleteItemPhotoAction.getResult()
                .getPhotos().contains(testBucketPhoto));
        assertActionSuccess(testListSubscriber, bucketListAction -> !Queryable.from(bucketListAction.getResult())
                .any(element -> {
                    return element.getPhotos().contains(testBucketPhoto);
                }));
        verifyBucketListStorage();
    }

    @Test
    public void markItemAsDoneTest() {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = subscribeBucketListChanges();
        TestSubscriber<ActionState<MarkItemAsDoneHttpAction>> testSubscriber = new TestSubscriber<>();

        bucketInteractor.marksAsDonePipe()
                .createObservable(new MarkItemAsDoneHttpAction(TEST_BUCKET_ITEM_UID, COMPLETED))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, markItemAsDoneAction -> TextUtils.equals(markItemAsDoneAction.getResponse()
                .getStatus(), COMPLETED));
        assertActionSuccess(testListSubscriber, bucketListAction -> Queryable.from(bucketListAction.getResult())
                .any(element -> TEST_BUCKET_ITEM_UID.equals(element.getUid())
                        && COMPLETED.equals(element.getStatus())));
        verifyBucketListStorage();
    }

    @Test
    public void findBucketItemByPhotoTest() {
        when(testBucketItem.getPhotos()).thenReturn(Lists.newArrayList(testBucketPhoto, mock(BucketPhoto.class)));

        TestSubscriber<ActionState<FindBucketItemByPhotoCommand>> testSubscriber = new TestSubscriber<>();

        bucketInteractor.bucketListActionPipe()
                .createObservable(BucketListCommand.fetch(false))
                .subscribe();
        bucketInteractor.findBucketItemByPhotoActionPipe()
                .createObservable(new FindBucketItemByPhotoCommand(testBucketPhoto))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, findBucketItemByPhotoAction -> findBucketItemByPhotoAction.getResult() != null);
    }

    @Override
    protected MockHttpActionService mockHttpService() {
        return new MockHttpActionService.Builder()
                .bind(new MockHttpActionService.Response(200).body(testBucketItem),
                        request -> TextUtils.equals(request.getMethod(), HttpAction.Method.POST.name())
                                && request.getUrl().endsWith("/api/bucket_list_items"))
                .bind(new MockHttpActionService.Response(200).body(testBucketItem),
                        request -> TextUtils.equals(request.getMethod(), HttpAction.Method.PATCH.name()))
                .bind(new MockHttpActionService.Response(200).body(new JsonObject()),
                        request -> TextUtils.equals(request.getMethod(), HttpAction.Method.DELETE.name()))
                .bind(new MockHttpActionService.Response(200).body(testBucketItem),
                        request -> request.getBody().toString().contains("id")
                                && TextUtils.equals(request.getMethod(), HttpAction.Method.PATCH.name()))
                .bind(new MockHttpActionService.Response(200).body(testBucketPhoto), request -> request.getUrl().contains("/photos"))
                .bind(new MockHttpActionService.Response(200).body(testPhotoUploadResponse), request -> request.getUrl().contains("/upload"))
                .build();
    }

    @Override
    protected Set<ActionStorage> storageSet() {
        when(uploadControllerStorage.getActionClass())
                .thenCallRealMethod();

        Set<ActionStorage> storageList = super.storageSet();
        storageList.add(uploadControllerStorage);

        return storageList;
    }

    private void checkBucketItemCreation(BucketBody body, Func1<CreateBucketItemHttpAction, Boolean> predicate) {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = subscribeBucketListChanges();
        TestSubscriber<ActionState<CreateBucketItemHttpAction>> testSubscriber = new TestSubscriber<>();

        bucketInteractor.createPipe()
                .createObservable(new CreateBucketItemHttpAction(body))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, predicate);
        assertActionSuccess(testListSubscriber, bucketListAction -> bucketListAction.getResult().contains(testBucketItem));
    }

    private void checkBucketItemUpdate(BucketBody body, Func1<BucketItem, Boolean> predicate) {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = subscribeBucketListChanges();
        TestSubscriber<ActionState<UpdateItemHttpAction>> testSubscriber = new TestSubscriber<>();
        bucketInteractor.updatePipe()
                .createObservable(new UpdateItemHttpAction(body))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, updateItemAction -> predicate.call(updateItemAction.getResponse()));
        assertActionSuccess(testListSubscriber, bucketListAction -> Queryable.from(bucketListAction.getResult())
                .any(predicate::call));
    }

    @NonNull
    private TestSubscriber<ActionState<BucketListCommand>> subscribeBucketListChanges() {
        TestSubscriber<ActionState<BucketListCommand>> testListSubscriber = new TestSubscriber<>();

        bucketInteractor.bucketListActionPipe().observe()
                .subscribe(testListSubscriber);
        return testListSubscriber;
    }

    @NonNull
    private TestSubscriber<ActionState<UploadPhotoControllerCommand>> subscribeCreateUploadController(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
        TestSubscriber<ActionState<UploadPhotoControllerCommand>> testSubscriber = new TestSubscriber<>();

        bucketInteractor.uploadControllerCommandPipe()
                .createObservable(UploadPhotoControllerCommand.create(TEST_BUCKET_ITEM_UID, photoEntityStateHolder))
                .subscribe(testSubscriber);
        return testSubscriber;
    }

    private boolean checkUploadingControllerByState(UploadPhotoControllerCommand command, EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
        List<EntityStateHolder<BucketPhoto>> listOfPhotos = command.getResult();
        return listOfPhotos.contains(photoEntityStateHolder);
    }

    @Nullable
    private String createFileAndGetPath() throws IOException {
        File testPhotoFile = folder.newFile("TestPhoto.jpeg");
        return testPhotoFile != null ? testPhotoFile.getPath() : null;
    }

    private EntityStateHolder<BucketPhoto> mockPhotoEntityHolderWithBehavior(EntityStateHolder.State state) {
        EntityStateHolder<BucketPhoto> testPhotoEntityStateHolder = createMockEntityStateHolder();

        when(testPhotoEntityStateHolder.entity())
                .thenReturn(testBucketPhoto);
        when(testPhotoEntityStateHolder.state())
                .thenReturn(state);

        return testPhotoEntityStateHolder;
    }

    @SuppressWarnings("unchecked")
    private <T> EntityStateHolder<T> createMockEntityStateHolder() {
        return mock(EntityStateHolder.class);
    }

    private void verifyBucketListStorage() {
        verify(mockDb).saveBucketList(any(), anyInt());
        verify(mockMemoryStorage).save(any(), anyList());
    }

    private void verifyUploadStorage() {
        verify(uploadControllerStorage).save(any(), anyList());
    }
}