package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;

import java.util.ArrayList;

public class CreateFeedPostPresenter extends CreateEntityPresenter<CreateFeedPostPresenter.View> {

    public CreateFeedPostPresenter() {
        priorityEventBus = 1;
    }

    public void removeImage() {
        cachedRemovedPhotoTags.clear();
        cachedAddedPhotoTags.clear();
        invalidateDynamicViews();
        view.attachPhotos(new ArrayList<>());
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
