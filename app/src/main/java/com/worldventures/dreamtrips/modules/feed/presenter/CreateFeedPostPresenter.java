package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;

import java.util.List;

public class CreateFeedPostPresenter extends CreateEntityPresenter<CreateFeedPostPresenter.View> {

    public CreateFeedPostPresenter() {
        priorityEventBus = 1;
    }

    public void onEvent(AttachPhotoEvent event) {
        if (view.isVisibleOnScreen() && event.getRequestType() != -1) {
            eventBus.cancelEventDelivery(event);
            pickImage(event.getRequestType());
        }
    }

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (view.isVisibleOnScreen() && event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);

            attachImages(Queryable.from(event.getImages()).toList(), event.getRequestType());
        }
    }

    public void removeImage() {
        cachedUploadTask = null;
        cachedRemovedPhotoTags.clear();
        cachedAddedPhotoTags.clear();
        invalidateDynamicViews();
        view.attachPhoto(null);
        view.enableImagePicker();
    }

    @Override
    public void attachImages(List<ChosenImage> photos, int requestType) {
        super.attachImages(photos, requestType);
        if (photos.size() != 0) view.disableImagePicker();
    }

    public interface View extends CreateEntityPresenter.View {

        void enableImagePicker();

        void disableImagePicker();
    }
}
