package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class PreviewPhotoTaggableHolderPresenter extends TaggableImageHolderPresenter {

    public PreviewPhotoTaggableHolderPresenter(Photo photo) {
        super(photo, false);
    }

    @Override
    public void deletePhotoTag(PhotoTag tag) {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(tag.getUser().getId());
        doRequest(new DeletePhotoTagsCommand(photo.getFSId(), userIds), aVoid -> {
            photo.getPhotoTags().remove(tag);
            onComplete();
        });
    }

    @Override
    public void pushRequests() {
        onComplete();
    }
}
