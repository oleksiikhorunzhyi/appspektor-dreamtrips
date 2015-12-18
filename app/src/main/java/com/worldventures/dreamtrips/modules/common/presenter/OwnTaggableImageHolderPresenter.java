package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

public class OwnTaggableImageHolderPresenter extends TaggableImageHolderPresenter {

    @State
    ArrayList<PhotoTag> newAddedTags;
    @State
    ArrayList<PhotoTag> newDeletedTags;

    private boolean addComplete, deleteComplete, updated;

    public OwnTaggableImageHolderPresenter(Photo photo, boolean canAddTags) {
        super(photo, canAddTags);
        if (newAddedTags == null && newDeletedTags == null) {
            newAddedTags = new ArrayList<>();
            newDeletedTags = new ArrayList<>();
        }
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

        if (!updated) {
            updatePhoto();
            return;
        }

        eventBus.post(new FeedEntityChangedEvent(photo));
        super.onComplete();
    }

    private void updatePhoto() {
        doRequest(new GetFeedEntityQuery(photo.getFSId()), entity -> {
            this.photo = (Photo) entity.getItem();
            updated = true;
            onComplete();
        });
    }

    @Override
    public void loadFriends(String query, TagView tagView) {
        doRequest(new GetFriendsQuery(null, query, 1 , 100), tagView::setUserFriends);
    }

    @Override
    public void restoreViewsIfNeeded() {
        super.restoreViewsIfNeeded();
        view.setupTags(newAddedTags);
        view.setupTags(newDeletedTags);
    }

    @Override
    public List<PhotoTag> getTagsToUpload() {
        return newAddedTags.size() > 0 ? newAddedTags : null;
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
