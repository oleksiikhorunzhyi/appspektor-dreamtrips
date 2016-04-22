package com.worldventures.dreamtrips.modules.common.presenter;

import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.event.PickerDoneEvent;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

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
            Queryable.from(event.getImages()).forEachR(image -> {
                Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, image.getFilePathOriginal());
                images.add(new PhotoGalleryModel(pair.first, pair.second));
            });
            mediaPickerManager.attach(new MediaAttachment(images, event.getRequestType(), requestId));
        }
    }

    public void attachImages(List<BasePhotoPickerModel> pickedImages, int type) {
        eventBus.post(new PickerDoneEvent());
        //
        Observable.from(pickedImages)
                .map(element -> {
                    Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, element.getOriginalPath());
                    return new PhotoGalleryModel(pair.first, pair.second);
                })
                .map(photoGalleryModel -> {
                    ArrayList<PhotoGalleryModel> chosenImages = new ArrayList<>();
                    chosenImages.add(photoGalleryModel);
                    return new MediaAttachment(chosenImages, type, requestId);
                })
                .compose(new IoToMainComposer<>())
                .subscribe(mediaAttachment -> {
                    mediaPickerManager.attach(mediaAttachment);
                }, error -> {
                    Timber.e(error, "");
                });
    }

    public interface View extends Presenter.View {

    }
}
