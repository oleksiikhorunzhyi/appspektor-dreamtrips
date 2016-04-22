package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.SocialUploaderyManager;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.api.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.UploadPhotosCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoEntity;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public abstract class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

    public static final int MAX_PHOTOS_COUNT = 15;

    @Inject
    MediaPickerManager mediaPickerManager;

    @Inject
    protected SocialUploaderyManager photoUploadManager;

    private Subscription mediaSubscription;
    private Subscription uploaderySubscription;
    private List<Subscription> mediaAttachmentSubscriptions = new ArrayList<>();

    @Override
    public void takeView(V view) {
        super.takeView(view);
        //
        mediaSubscription = mediaPickerManager
                .toObservable()
                .filter(attachment -> attachment.requestId == getMediaRequestId())
                .subscribe(this::attachImages);

        Observable<ActionState<UploaderyImageCommand>> observable = photoUploadManager
                .getTaskChangingObservable()
                .compose(new IoToMainComposer<>());
        //
        uploaderySubscription = observable.subscribe(state -> {
            PhotoCreationItem item = getPhotoCreationItemById(state.action.getCommandId());
            if (item != null) {
                item.setStatus(state.status);
                if (state.status == ActionState.Status.SUCCESS) {
                    item.setOriginUrl(((SimpleUploaderyCommand) state.action).getResult().getPhotoUploadResponse().getLocation());
                    invalidateDynamicViews();
                } else if (state.status == ActionState.Status.FAIL) {
                    invalidateDynamicViews();
                }
                view.updateItem(item);
            }
        });
    }

    protected PhotoCreationItem getPhotoCreationItemById(int id) {
        return Queryable.from(cachedCreationItems).firstOrDefault(cachedTask -> cachedTask.getId() == id);
    }

    public abstract int getMediaRequestId();

    @Override
    public void dropView() {
        super.dropView();
        //
        if (mediaSubscription != null && !mediaSubscription.isUnsubscribed())
            mediaSubscription.unsubscribe();
        if (uploaderySubscription != null && !uploaderySubscription.isUnsubscribed())
            uploaderySubscription.unsubscribe();
        Queryable.from(mediaAttachmentSubscriptions).forEachR(subscription -> {
            if (subscription != null && !subscription.isUnsubscribed())
                subscription.unsubscribe();
        });
    }

    @Override
    protected void updateUi() {
        super.updateUi();
        //
        if (!isCachedUploadTaskEmpty()) view.attachPhotos(cachedCreationItems);
        //
        invalidateDynamicViews();
    }

    @Override
    protected boolean isChanged() {
        return !isCachedTextEmpty()
                || (cachedCreationItems.size() > 0 && isEntitiesReadyToPost());
    }

    @Override
    public void post() {
        if (!isCachedTextEmpty() && isCachedUploadTaskEmpty()) {
            createPost(null);
        } else {
            CreatePhotoEntity createPhotoEntity = new CreatePhotoEntity();
            Queryable.from(cachedCreationItems).forEachR(item -> createPhotoEntity
                    .addPhoto(new CreatePhotoEntity.PhotoEntity.Builder()
                            .originUrl(item.getOriginUrl())
                            .title(item.getTitle())
                            .width(item.getWidth())
                            .height(item.getHeight())
                            .date(Calendar.getInstance().getTime())
                            .coordinates(location != null ? new CreatePhotoEntity.Coordinates(location.getLat(), location.getLng()) : null)
                            .locationName(location != null ? location.getName() : null)
                            .photoTags(item.getCachedAddedPhotoTags())
                            .build()));
            if (!createPhotoEntity.isEmpty()) {
                doRequest(new UploadPhotosCommand(createPhotoEntity), this::createPost);
            }
        }
    }

    private void createPost(List<Photo> photos) {
        CreatePhotoPostEntity createPhotoPostEntity = new CreatePhotoPostEntity();
        createPhotoPostEntity.setDescription(cachedText);
        createPhotoPostEntity.setLocation(location);
        if (photos != null)
            Queryable.from(photos).forEachR(photo -> createPhotoPostEntity
                    .addAttachment(new CreatePhotoPostEntity.Attachment(photo.getUid())));
        doRequest(new CreatePostCommand(createPhotoPostEntity), this::processPostSuccess);
    }

    @Override
    protected void processPostSuccess(FeedEntity feedEntity) {
        super.processPostSuccess(feedEntity);
        eventBus.post(new FeedItemAddedEvent(FeedItem.create(feedEntity, getAccount())));
    }

    public int getRemainingPhotosCount() {
        return MAX_PHOTOS_COUNT - cachedCreationItems.size();
    }

    public boolean removeImage(PhotoCreationItem item) {
        boolean removed = cachedCreationItems.remove(item);
        if (removed) {
            invalidateDynamicViews();
            view.enableImagePicker();
        }
        return removed;
    }

    public void attachImages(MediaAttachment mediaAttachment) {
        if (mediaAttachment.chosenImages == null || mediaAttachment.chosenImages.size() == 0) {
            return;
        }
        //
        if (cachedCreationItems.size() + mediaAttachment.chosenImages.size() == MAX_PHOTOS_COUNT)
            view.disableImagePicker();
        //
        imageSelected(mediaAttachment);
    }

    private void imageSelected(MediaAttachment mediaAttachment) {
        if (view != null) {
            mediaAttachmentSubscriptions.add(Observable.from(mediaAttachment.chosenImages)
                    .concatMap(this::convertPhotoCreationItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newImage -> {
                        cachedCreationItems.add(newImage);
                        if (view != null) {
                            view.attachPhoto(newImage);
                            if (ValidationUtils.isUrl(newImage.getFilePath())) {
                                doRequest(new CopyFileCommand(context, newImage.getFilePath()), s -> {
                                    newImage.setFilePath(s);
                                    startUpload(newImage);
                                });
                            } else {
                                startUpload(newImage);
                            }
                        }
                    }, throwable -> {
                        Timber.e(throwable, "");
                    }));
        }
    }

    @NonNull
    private Observable<PhotoCreationItem> convertPhotoCreationItem(PhotoGalleryModel photoGalleryModel) {
        return ImageUtils.getBitmap(context, Uri.parse(photoGalleryModel.getThumbnailPath()), 300, 300)
                .subscribeOn(Schedulers.io())
                .compose(bitmapObservable -> Observable.zip(ImageUtils.getRecognizedFaces(context, bitmapObservable), bitmapObservable, (photoTags, bitmap) -> new Pair<>(bitmap, photoTags)))
                .map(pair -> {
                    PhotoCreationItem item = new PhotoCreationItem();
                    item.setFilePath(photoGalleryModel.getThumbnailPath());
                    item.setStatus(ActionState.Status.START);
                    Size imageSize = photoGalleryModel.getSize();
                    item.setWidth(imageSize != null ? imageSize.getWidth() : pair.first.getWidth());
                    item.setHeight(imageSize != null ? imageSize.getHeight() : pair.first.getHeight());
                    item.setSuggestions(pair.second);
                    item.setCanDelete(true);
                    item.setCanEdit(true);
                    return item;
                });
    }

    protected boolean isCachedUploadTaskEmpty() {
        return cachedCreationItems.size() == 0;
    }

    protected boolean isEntitiesReadyToPost() {
        return Queryable.from(cachedCreationItems).firstOrDefault(item -> item.getStatus() != ActionState.Status.SUCCESS) == null;
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    public void startUpload(PhotoCreationItem item) {
        UploadTask uploadTask = item.toUploadTask();
        photoUploadManager.upload(uploadTask.getFilePath());
        item.setId(uploadTask.getFilePath().hashCode());
    }

    public interface View extends ActionEntityPresenter.View {

        void enableImagePicker();

        void disableImagePicker();
    }
}
