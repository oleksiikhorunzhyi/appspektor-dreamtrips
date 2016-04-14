package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MediaPickerPresenter extends Presenter<MediaPickerPresenter.View> {

    public static final int REQUESTER_ID = -10;

    private int requestId;

    @Inject
    MediaPickerManager mediaPickerManager;
    @Inject
    DrawableUtil drawableUtil;

    public MediaPickerPresenter(int requestId) {
        this.requestId = requestId;
    }

    public void onEvent(ImagePickedEvent event) {
        if (view.isVisibleOnScreen() && event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);

            List<PhotoGalleryModel> images = new ArrayList<>();
            Queryable.from(event.getImages()).forEachR(image -> images.add(new PhotoGalleryModel(image.getFilePathOriginal())));
            mediaPickerManager.attach(new MediaAttachment(images, event.getRequestType(), requestId));
        }
    }

    public void attachImages(List<BasePhotoPickerModel> pickedImages, int type) {
        Observable.from(pickedImages)
                .map(element -> {
                    ChosenImage chosenImage = new ChosenImage();
                    chosenImage.setFileThumbnail(ImageUtils.generateUri(drawableUtil, element.getOriginalPath()));
                    chosenImage.setFilePathOriginal(element.getOriginalPath());
                    return chosenImage;
                })
                .map(image -> new PhotoGalleryModel(image.getFilePathOriginal()))
                .map(photoGalleryModel -> {
                    ArrayList<PhotoGalleryModel> chosenImages = new ArrayList<>();
                    chosenImages.add(photoGalleryModel);
                    return new MediaAttachment(chosenImages, type, requestId);
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaAttachment -> {
                    mediaPickerManager.attach(mediaAttachment);
                });
    }

    public interface View extends Presenter.View {

    }
}
