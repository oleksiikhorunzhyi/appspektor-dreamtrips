package com.worldventures.dreamtrips.modules.common.presenter;

import android.graphics.RectF;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

public abstract class TaggableImageHolderPresenter<VIEW extends TaggableImageHolderPresenter.View> extends Presenter<VIEW> {

    @Nullable
    protected Photo photo;

    public TaggableImageHolderPresenter(Photo photo) {
        this.photo = photo;
    }

    @Override
    public void takeView(VIEW view) {
        super.takeView(view);
    }

    public void setupTags() {
        if (photo != null && photo.getPhotoTags() != null) view.addTags(photo.getPhotoTags());
    }

    public abstract void deletePhotoTag(PhotoTag tag);

    public Photo getPhoto() {
        return photo;
    }

    public interface View extends Presenter.View {

        void addTags(List<PhotoTag> tags);

        RectF getImageBounds();

    }
}
