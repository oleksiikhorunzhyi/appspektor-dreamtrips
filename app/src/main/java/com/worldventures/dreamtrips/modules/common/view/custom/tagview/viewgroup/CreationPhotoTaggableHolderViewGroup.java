package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.presenter.CreationPhotoTaggableHolderPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.List;

public class CreationPhotoTaggableHolderViewGroup extends TaggableImageViewGroup<CreationPhotoTaggableHolderPresenter> implements CreationPhotoTaggableHolderPresenter.View {

    private GestureDetector gestureDetector;
    private TaggableCompleteListener completeListener;


    public CreationPhotoTaggableHolderViewGroup(Context context) {
        super(context);
    }

    public CreationPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreationPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    protected CreationPhotoTaggableHolderPresenter getPresenter(Photo photo) {
        return new CreationPhotoTaggableHolderPresenter(photo);
    }

    @Override
    public void setup(Injector injector, Photo photo) {
        super.setup(injector, photo);
        gestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());
        presenter.restoreViewsIfNeeded();
    }

    public List<PhotoTag> getTagsToUpload() {
        return presenter.getTagsToUpload();
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            boolean confirmed = imageBounds.contains(event.getX(), event.getY());
            if (confirmed) {
                removeUncompletedViews();
                addTagView(event.getX(), event.getY());
            }
            return confirmed;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    protected void addTagView(float x, float y) {
        addTagView(new PhotoTag(new PhotoTag.TagPosition(x, y, x, y), null));
    }

    @Override
    public void onRequestsComplete() {
        if (completeListener != null)
            completeListener.onTagRequestsComplete();
    }

    public void setCompleteListener(TaggableCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void pushRequests() {
        presenter.pushRequests();
    }

    public interface TaggableCompleteListener {

        void onTagRequestsComplete();
    }

}
