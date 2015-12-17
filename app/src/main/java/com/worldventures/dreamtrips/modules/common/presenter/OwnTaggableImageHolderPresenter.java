package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class OwnTaggableImageHolderPresenter extends TaggableImageHolderPresenter {

    private List<PhotoTag> newAddedTags;
    private List<PhotoTag> newDeletedTags;

    private boolean addComplete, deleteComplete;

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
        if (newAddedTags.size() > 0) {
            Queryable.from(newAddedTags).forEachR(tag -> {
                PhotoTag.TagPosition newTagPosition = CoordinatesTransformer
                        .convertToProportional(tag.getPosition(), view.getImageBounds());
                tag.setTagPosition(newTagPosition);
            });
            doRequest(new AddPhotoTagsCommand(photo.getFSId(), newAddedTags), aVoid -> {
                newAddedTags.clear();
                addComplete = true;
                onComplete();
            });
        } else {
            addComplete = true;
        }
        //
        if (newDeletedTags.size() > 0) {
            List<Integer> userIds = Queryable.from(newDeletedTags).map(photo -> photo.getUser().getId()).toList();
            doRequest(new DeletePhotoTagsCommand(photo.getFSId(), userIds), aVoid -> {
                newDeletedTags.clear();
                deleteComplete = true;
                onComplete();
            });
        } else {
            deleteComplete = true;
        }

        onComplete();
    }

    @Override
    public void onComplete() {
        if (!addComplete || !deleteComplete) return;
        super.onComplete();
    }

    @Override
    public void loadFriends(String query, TagView tagView) {
        doRequest(new GetFriendsQuery(null, query, 1 , 5), tagView::setUserFriends);
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
