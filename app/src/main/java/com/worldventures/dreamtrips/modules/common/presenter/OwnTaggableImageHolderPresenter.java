package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class OwnTaggableImageHolderPresenter extends TaggableImageHolderPresenter {

    private List<PhotoTag> newAddedTags;
    private List<PhotoTag> newDeletedTags;

    public OwnTaggableImageHolderPresenter(Photo photo) {
        super(photo);
        newAddedTags = new ArrayList<>();
        newDeletedTags = new ArrayList<>();
    }

    @Override
    public void addPhotoTag(PhotoTag tag) {
        newAddedTags.add(tag);
    }

    @Override
    public void deletePhotoTag(PhotoTag tag) {
        if (newAddedTags.contains(tag)) {
            newAddedTags.remove(tag);
            return;
        }

        newDeletedTags.add(tag);
    }

    @Override
    public void pushRequests() {
        if (newAddedTags.size() > 0)
            doRequest(new AddPhotoTagsCommand(photo.getFSId(), newAddedTags), aVoid -> newAddedTags.clear());
        //
        if (newDeletedTags.size() > 0) {
            List<Integer> userIds = Queryable.from(newDeletedTags).map(PhotoTag::getTargetUserId).toList();
            doRequest(new DeletePhotoTagsCommand(photo.getFSId(), userIds), aVoid -> newDeletedTags.clear());
        }
    }

    @Override
    public boolean isOwnPhoto() {
        return true;
    }

    @Override
    public boolean isViewCanBeDeleted(int userId) {
        return true;
    }

}
