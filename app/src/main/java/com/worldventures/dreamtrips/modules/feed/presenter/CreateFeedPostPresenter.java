package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;

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
    public void attachImages(MediaAttachment mediaAttachment) {
        super.attachImages(mediaAttachment);
        if (mediaAttachment.chosenImages != null && mediaAttachment.chosenImages.size() != 0) view.disableImagePicker();
    }

    public interface View extends CreateEntityPresenter.View {

        void enableImagePicker();

        void disableImagePicker();
    }
}
