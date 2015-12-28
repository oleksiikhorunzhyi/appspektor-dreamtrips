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

public class CreationPhotoTaggableHolderPresenter extends TaggableImageHolderPresenter<CreationPhotoTaggableHolderPresenter.View> {

    private boolean addComplete, deleteComplete, updated;

    public CreationPhotoTaggableHolderPresenter(Photo photo) {
        super(photo);
    }

    public void addPhotoTag(PhotoTag tag) {
       view.addTag(tag);
    }


    @Override
    public void deletePhotoTag(PhotoTag tag) {
        view.deleteTag(tag);
    }

    public void pushRequests() {
        ArrayList<PhotoTag> newAddedTags = view.getLocallyAddedTags();
        ArrayList<PhotoTag> newDeletedTags = view.getLocallyDeletedTags();

        if (newAddedTags.size() > 0) {
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

    public void onComplete() {
        if (!addComplete || !deleteComplete) return;

        if (!updated) {
            updatePhoto();
            return;
        }

        eventBus.post(new FeedEntityChangedEvent(photo));
        view.onRequestsComplete();
    }

    private void updatePhoto() {
        doRequest(new GetFeedEntityQuery(photo.getFSId()), entity -> {
            this.photo = (Photo) entity.getItem();
            updated = true;
            onComplete();
        });
    }

    public void loadFriends(String query, TagView tagView) {
        doRequest(new GetFriendsQuery(null, query, 1, 100), tagView::setUserFriends);
    }

    public interface View extends TaggableImageHolderPresenter.View {
        void onRequestsComplete();

        void addTag(PhotoTag tag);

        void deleteTag(PhotoTag tag);

        ArrayList<PhotoTag> getLocallyAddedTags();

        ArrayList<PhotoTag> getLocallyDeletedTags();
    }

}
