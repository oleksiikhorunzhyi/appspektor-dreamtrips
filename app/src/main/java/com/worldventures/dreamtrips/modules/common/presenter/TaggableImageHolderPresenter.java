package com.worldventures.dreamtrips.modules.common.presenter;

import android.graphics.RectF;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

public abstract class TaggableImageHolderPresenter extends Presenter<TaggableImageHolderPresenter.View> {

    protected Photo photo;
    protected boolean canAddTags;

    public TaggableImageHolderPresenter(Photo photo, boolean canAddTags) {
        this.photo = photo;
        this.canAddTags = canAddTags;
    }

    public static TaggableImageHolderPresenter create(Photo photo, boolean isOwnPhoto, boolean canAddTags) {
        if (isOwnPhoto) {
            return new OwnTaggableImageHolderPresenter(photo, canAddTags);
        }

        return new ForeignTaggableImageHolderPresenter(photo);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
    }

    public void setupTags() {
        if (photo != null && photo.getPhotoTags() != null) view.setupTags(photo.getPhotoTags());
    }

    public boolean canAddTags() {
        return canAddTags;
    }

    public void addPhotoTag(PhotoTag tag) {
    }

    public void deletePhotoTag(PhotoTag tag) {
    }

    public void pushRequests() {
    }

    public void loadFriends(String query, TagView view) {
    }

    public void restoreViewsIfNeeded() {
    }

    public List<PhotoTag> getTagsToUpload() {
        return null;
    }

    public void onComplete() {
        view.onRequestsComplete();
    }

    public abstract boolean isOwnPhoto();

    public abstract boolean isViewCanBeDeleted(int userId);

    public interface View extends Presenter.View {

        void setupTags(List<PhotoTag> tags);

        RectF getImageBounds();

        void onRequestsComplete();
    }
}
