package com.worldventures.dreamtrips.modules.common.presenter;

import android.graphics.RectF;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

public abstract class TaggableImageHolderPresenter extends Presenter<TaggableImageHolderPresenter.View> {

    protected Photo photo;

    public TaggableImageHolderPresenter(Photo photo) {
        this.photo = photo;
    }

    public static TaggableImageHolderPresenter create(Photo photo, boolean isOwnPhoto) {
        if (isOwnPhoto) {
            return new OwnTaggableImageHolderPresenter(photo);
        }

        return new ForeignTaggableImageHolderPresenter(photo);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
    }

    public void setupTags() {
        if (photo.getPhotoTags() != null) view.setupTags(photo.getPhotoTags());
    }

    public void addPhotoTag(PhotoTag tag) {
    }

    public void deletePhotoTag(PhotoTag tag) {
    }

    public void pushRequests() {
    }

    public void loadFriends(String query, TagView view) {
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
