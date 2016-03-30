package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MediaPickerPresenter extends Presenter<MediaPickerPresenter.View> {

    public static final int REQUESTER_ID = -10;

    private int requestId;

    @Inject
    MediaPickerManager mediaPickerManager;

    public MediaPickerPresenter(int requestId) {
        this.requestId = requestId;
    }

    public void onEvent(AttachPhotoEvent event) {
        if (view.isVisibleOnScreen() && event.getRequestType() != -1)
            pickImage(event.getRequestType());
    }

    public void pickImage(int requestType) {
        if (view.isVisibleOnScreen())
            eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (view.isVisibleOnScreen() && event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);

            attachImages(Queryable.from(event.getImages()).toList(), event.getRequestType());
        }
    }

    public void attachImages(List<ChosenImage> chosenImages, int type) {
        List<PhotoGalleryModel> images = new ArrayList<>();
        Queryable.from(chosenImages).forEachR(image -> images.add(new PhotoGalleryModel(image.getFilePathOriginal())));
        mediaPickerManager.attach(new MediaAttachment(images, type, requestId));
    }

    public interface View extends Presenter.View {

    }
}
