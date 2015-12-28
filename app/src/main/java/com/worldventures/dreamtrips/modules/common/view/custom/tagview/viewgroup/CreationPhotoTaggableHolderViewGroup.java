package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.presenter.CreationPhotoTaggableHolderPresenter;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

public class CreationPhotoTaggableHolderViewGroup extends TaggableImageViewGroup<CreationPhotoTaggableHolderPresenter>
        implements CreationPhotoTaggableHolderPresenter.View {

    @State
    ArrayList<PhotoTag> locallyAddedTags = new ArrayList<>();
    @State
    ArrayList<PhotoTag> locallyDeletedTags = new ArrayList<>();

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
    }

    public List<PhotoTag> getTagsToUpload() {
        if (locallyAddedTags.size() > 0) {
            return locallyAddedTags;
        }

        return null;
    }

    public void restoreState() {
        addTags(locallyAddedTags);
        addTags(locallyDeletedTags);
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
        addTagView(new PhotoTag(CoordinatesTransformer.convertToProportional(new PhotoTag.TagPosition(x, y, x, y), getImageBounds()), null));
    }

    @Override
    public void onRequestsComplete() {
        if (completeListener != null)
            completeListener.onTagRequestsComplete();
    }

    @Override
    public void addTag(PhotoTag tag) {
        locallyAddedTags.add(tag);
    }

    @Override
    public void deleteTag(PhotoTag tag) {
        if (locallyAddedTags.contains(tag)) {
            locallyAddedTags.remove(tag);
            return;
        }
        locallyDeletedTags.add(tag);
    }

    @Override
    public ArrayList<PhotoTag> getLocallyAddedTags() {
        return locallyAddedTags;
    }

    @Override
    public ArrayList<PhotoTag> getLocallyDeletedTags() {
        return locallyDeletedTags;
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
