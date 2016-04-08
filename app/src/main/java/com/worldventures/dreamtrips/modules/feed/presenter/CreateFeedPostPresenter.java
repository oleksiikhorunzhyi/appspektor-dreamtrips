package com.worldventures.dreamtrips.modules.feed.presenter;

import com.kbeanie.imagechooser.api.ChosenImage;

import java.util.List;

public class CreateFeedPostPresenter extends CreateEntityPresenter<CreateFeedPostPresenter.View> {

    public CreateFeedPostPresenter() {
        priorityEventBus = 1;
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
    public int getMediaRequestId() {
        return CreateFeedPostPresenter.class.getSimpleName().hashCode();
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
