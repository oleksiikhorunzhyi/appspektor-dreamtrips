package com.worldventures.dreamtrips.modules.common.presenter;

import android.support.v4.view.ViewCompat;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.CreationTagView;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class CreationPhotoTaggableHolderPresenter extends TaggableImageHolderPresenter<CreationPhotoTaggableHolderPresenter.View> {

    private static final int PAGE_SIZE = 100;

    private boolean addComplete, deleteComplete, updated;

    public CreationPhotoTaggableHolderPresenter(Photo photo) {
        super(photo);
    }

    @Override
    public void showExistingTags() {
        view.showSuggestions();
        super.showExistingTags();
    }

    @Override
    public void deletePhotoTag(PhotoTag tag) {
        view.deleteTag(tag);
        view.showSuggestions();
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
            List<Integer> userIds = Queryable.from(newDeletedTags).map(photo -> photo.getTargetUserId()).toList();
            doRequest(new DeletePhotoTagsCommand(photo.getFSId(), userIds), aVoid -> {
                newDeletedTags.clear();
                deleteComplete = true;
                List<PhotoTag> tags = Queryable.from(photo.getPhotoTags())
                        .filter(tag -> userIds.contains(tag.getTargetUserId())).toList();
                photo.getPhotoTags().removeAll(tags);
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

    public void loadFriends(String query, int nextPage, CreationTagView tagView) {
        doRequest(new GetFriendsQuery(null, query, nextPage, PAGE_SIZE), friends -> {
            if (ViewCompat.isAttachedToWindow(tagView)) {
                tagView.setUserFriends(Queryable.from(friends).filter(user -> !isUserExists(user)).toList());
            }
        });
    }

    private boolean isUserExists(User user) {
        boolean containsOnServer = photo != null && isContainUser(photo.getPhotoTags(), user);
        boolean containsUserInLocallyAdded = isContainUser(view.getLocallyAddedTags(), user);
        boolean containUserInDeleted = isContainUser(view.getLocallyDeletedTags(), user);
        return containsUserInLocallyAdded || (containsOnServer && !containUserInDeleted);
    }

    private boolean isContainUser(List<PhotoTag> tagList, User user) {
        return Queryable.from(tagList).map(PhotoTag::getTargetUserId).contains(user.getId());
    }

    public interface View extends TaggableImageHolderPresenter.View {
        void onRequestsComplete();

        void addTag(PhotoTag tag);

        void deleteTag(PhotoTag tag);

        ArrayList<PhotoTag> getLocallyAddedTags();

        ArrayList<PhotoTag> getLocallyDeletedTags();

        void showSuggestions();
    }

}
