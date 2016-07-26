package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemPhotoAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketPostBody;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenter.View> {

    public static final int BUCKET_MEDIA_REQUEST_ID = BucketItemEditPresenter.class.getSimpleName().hashCode();

    @Inject
    MediaPickerManager mediaPickerManager;

    private Date selectedDate;

    private boolean savingItem = false;

    private Set<AddBucketItemPhotoCommand> operationList = new HashSet<>();

    public BucketItemEditPresenter(BucketBundle bundle) {
        super(bundle);
    }

    @Override
    public void takeView(View view) {
        priorityEventBus = 1;
        super.takeView(view);
        bindObservables(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedDate = bucketItem.getTargetDate();
        List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
        if (!list.isEmpty()) {
            view.setCategoryItems(list);
            view.setCategory(list.indexOf(bucketItem.getCategory()));
        }
    }

    @Override
    protected void syncUI() {
        super.syncUI();
        view.bind(bucketInteractor.mergeBucketItemPhotosWithStorageCommandPipe()
                .createObservableResult(new MergeBucketItemPhotosWithStorageCommand(bucketItem.getUid(), bucketItem.getPhotos()))
                .map(Command::getResult)
                .observeOn(Schedulers.immediate()))
                .subscribe(entityStateHolders -> {
                    view.setImages(entityStateHolders);
                });
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.hideLoading();
    }

    public void saveItem() {
        view.showLoading();
        savingItem = true;

        view.bind(bucketInteractor.updatePipe()
                .createObservableResult(new UpdateItemHttpAction(createBucketPostBody()))
                .observeOn(AndroidSchedulers.mainThread())
                .map(UpdateItemHttpAction::getResponse))
                .subscribe(item -> {
                    if (savingItem) {
                        savingItem = false;
                        view.done();
                    }
                }, this::handleError);
    }

    public Date getDate() {
        if (bucketItem.getTargetDate() != null) {
            return bucketItem.getTargetDate();
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    public void deletePhotoRequest(BucketPhoto bucketPhoto) {
        if (!bucketItem.getPhotos().isEmpty() && bucketItem.getPhotos().contains(bucketPhoto)) {
            view.bind(bucketInteractor.deleteItemPhotoPipe()
                    .createObservable(new DeleteItemPhotoCommand(bucketItem, bucketPhoto))
                    .observeOn(AndroidSchedulers.mainThread()))
                    .subscribe(new ActionStateSubscriber<DeleteItemPhotoCommand>()
                            .onSuccess(deleteItemPhotoAction -> view.deleteImage(EntityStateHolder.create(bucketPhoto, EntityStateHolder.State.DONE)))
                            .onFail((deleteItemPhotoAction, throwable) -> handleError(throwable)));
        }
    }

    public void onDateSet(int year, int month, int day) {
        String date = DateTimeUtils.convertDateToString(year, month, day);
        view.setTime(date);
        setDate(DateTimeUtils.dateFromString(date));
    }

    public void setDate(Date date) {
        this.selectedDate = date;
    }

    public void onDateClear() {
        view.setTime(context.getString(R.string.someday));
        setDate(null);
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////
    public void onUploadTaskClicked(EntityStateHolder<BucketPhoto> photoStateHolder) {
        view.deleteImage(photoStateHolder);

        EntityStateHolder.State state = photoStateHolder.state();
        switch (state) {
            case FAIL:
                startUpload(photoStateHolder);
                break;
            case PROGRESS:
                cancelUpload(photoStateHolder);
                break;
        }
    }

    private void startUpload(EntityStateHolder<BucketPhoto> photoStateHolder) {
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START, "", bucketItem.getType());
        eventBus.post(new BucketItemPhotoAnalyticEvent(TrackingHelper.ATTRIBUTE_UPLOAD_PHOTO, bucketItem.getUid()));

        imageSelected(Uri.parse(photoStateHolder.entity().getImagePath()));
    }

    private void cancelUpload(EntityStateHolder<BucketPhoto> photoStateHolder) {
        bucketInteractor.addBucketItemPhotoPipe().cancel(findCommandByStateHolder(photoStateHolder));
    }

    private void attachImages(List<PhotoGalleryModel> chosenImages) {
        if (chosenImages.size() == 0) {
            return;
        }
        Queryable.from(chosenImages).forEachR(choseImage ->
                imageSelected(Uri.parse(choseImage.getThumbnailPath())));
    }

    private void imageSelected(Uri uri) {
        bucketInteractor.addBucketItemPhotoPipe().send(new AddBucketItemPhotoCommand(bucketItem, uri.toString()));
    }

    private void bindObservables(View view) {
        view.bind(bucketInteractor.addBucketItemPhotoPipe()
                .observe()
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(new ActionStateSubscriber<AddBucketItemPhotoCommand>()
                        .onStart(command -> {
                            operationList.add(command);
                            view.addItemInProgressState(command.photoEntityStateHolder());
                        })
                        .onSuccess(command -> {
                            operationList.remove(command);
                            view.changeItemState(command.photoEntityStateHolder());
                        })
                        .onFail((command, throwable) -> {
                            operationList.remove(command);
                            if (throwable instanceof CancelException) {
                                return;
                            }

                            view.changeItemState(command.photoEntityStateHolder());
                        }));

        view.bind(Observable.merge(bucketInteractor.updatePipe().observeSuccess()
                        .map(UpdateItemHttpAction::getResponse),
                bucketInteractor.addBucketItemPhotoPipe().observeSuccess()
                        .map(addBucketItemPhotoCommand -> addBucketItemPhotoCommand.getResult().first),
                bucketInteractor.deleteItemPhotoPipe().observeSuccess()
                        .map(DeleteItemPhotoCommand::getResult)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bucketItem -> {
                    eventBus.post(new FeedEntityChangedEvent(bucketItem)); //TODO fix it when feed would be rewrote
                });
        //
        view.bind(mediaPickerManager.toObservable()
                .filter(attachment -> attachment.requestId == BUCKET_MEDIA_REQUEST_ID))
                .subscribe(mediaAttachment -> attachImages(mediaAttachment.chosenImages));
    }

    //////////////////////
    // Common
    //////////////////////
    private int getCategoryId() {
        CategoryItem categoryItem = view.getSelectedItem();
        return categoryItem != null ? categoryItem.getId() : 0;
    }

    private String getDateAsString(Date date) {
        return DateTimeUtils.convertDateToString(date, DateTimeUtils.DEFAULT_ISO_FORMAT);
    }

    @NonNull
    private ImmutableBucketPostBody createBucketPostBody() {
        return ImmutableBucketPostBody.builder()
                .id(bucketItem.getUid())
                .name(view.getTitle())
                .description(view.getDescription())
                .status(view.getStatus() ? BucketItem.COMPLETED : BucketItem.NEW)
                .tags(TextUtils.getListFromString(view.getTags()))
                .friends(TextUtils.getListFromString(view.getPeople()))
                .categoryId(getCategoryId())
                .date(getDateAsString(selectedDate))
                .build();
    }

    private AddBucketItemPhotoCommand findCommandByStateHolder(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
        return Queryable.from(operationList)
                .firstOrDefault(element -> element.photoEntityStateHolder().equals(photoEntityStateHolder));
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void showError();

        void setCategory(int selection);

        void setCategoryItems(List<CategoryItem> items);

        CategoryItem getSelectedItem();

        boolean getStatus();

        String getTags();

        String getPeople();

        String getTitle();

        String getDescription();

        void hideMediaPicker();

        void showMediaPicker();

        void showLoading();

        void hideLoading();

        void addItemInProgressState(EntityStateHolder<BucketPhoto> photoEntityStateHolder);

        void changeItemState(EntityStateHolder<BucketPhoto> photoEntityStateHolder);

        void deleteImage(EntityStateHolder<BucketPhoto> photoStateHolder);
    }
}