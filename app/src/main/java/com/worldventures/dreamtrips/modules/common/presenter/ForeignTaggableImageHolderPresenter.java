package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class ForeignTaggableImageHolderPresenter extends TaggableImageHolderPresenter {

    public ForeignTaggableImageHolderPresenter(Photo photo) {
        super(photo, false);
    }

    @Override
    public void deletePhotoTag(PhotoTag tag) {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(tag.getUser().getId());
        doRequest(new DeletePhotoTagsCommand(photo.getFSId(), userIds));
    }

    @Override
    public void pushRequests() {
        onComplete();
    }

    @Override
    public boolean isOwnPhoto() {
        return false;
    }

    @Override
    public boolean isViewCanBeDeleted(int userId) {
        return getAccount().getId() == userId;
    }
}
