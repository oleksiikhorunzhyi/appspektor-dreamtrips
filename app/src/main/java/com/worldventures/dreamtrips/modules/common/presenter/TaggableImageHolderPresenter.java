package com.worldventures.dreamtrips.modules.common.presenter;

import android.graphics.RectF;

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

    public static TaggableImageHolderPresenter create(Photo photo, boolean canAddTags) {
        if (canAddTags) {
            return new CreationPhotoTaggableHolderPresenter(photo, canAddTags);
        }
        return new PreviewPhotoTaggableHolderPresenter(photo);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
    }

    public void setupTags() {
        if (photo != null && photo.getPhotoTags() != null) view.addTags(photo.getPhotoTags());
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

    public boolean isOwnPhoto() {
        return getAccount().getId() == photo.getOwner().getId();
    }

    public final boolean isViewCanBeDeleted(int userId) {
        return isOwnPhoto() || getAccount().getId() == userId;
    }

    public interface View extends Presenter.View {

        void addTags(List<PhotoTag> tags);

        RectF getImageBounds();

        void onRequestsComplete();
    }
}
