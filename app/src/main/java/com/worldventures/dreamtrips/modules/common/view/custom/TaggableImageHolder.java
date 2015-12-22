package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.presenter.TaggableImageHolderPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.ExistsTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.NewTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

public class TaggableImageHolder extends RelativeLayout implements TaggableImageHolderPresenter.View {

    private TaggableImageHolderPresenter presenter;
    private boolean setuped;

    private TaggableCompleteListener completeListener;

    private GestureDetector gestureDetector;

    private RectF imageBounds;

    public TaggableImageHolder(Context context) {
        super(context);
    }

    public TaggableImageHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaggableImageHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TaggableImageHolder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setup(Injector injector, Photo photo, boolean isOwnPhoto, boolean canAddTags) {
        presenter = TaggableImageHolderPresenter.create(photo, isOwnPhoto, canAddTags);
        injector.inject(presenter);
        presenter.takeView(this);
        presenter.onStart();
        setuped = true;
        //
        gestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());
        presenter.restoreViewsIfNeeded();
    }

    public boolean isSetuped() {
        return setuped;
    }

    public void setCompleteListener(TaggableCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void hide() {
        removeUncompletedViews();
        //
        setVisibility(View.INVISIBLE);
    }

    public void show(RectF imageBounds) {
        this.imageBounds = imageBounds;
        setVisibility(View.VISIBLE);
        presenter.setupTags();
    }

    public void pushRequests() {
        presenter.pushRequests();
    }

    public List<PhotoTag> getTagsToUpload() {
        return presenter.getTagsToUpload();
    }

    @Override
    protected void onDetachedFromWindow() {
        hide();
        if (presenter != null) {
            presenter.onStop();
            presenter.dropView();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        return true;
    }

    private void addTagView(float x, float y) {
        addTagView(new PhotoTag(-1, new PhotoTag.TagPosition(x, y, x, y)));
    }

    private void addTagView(PhotoTag photoTag) {
        TagView view = TagView.create(getContext(), photoTag);
        view.setTagListener(new TagListener() {

            @Override
            public void onQueryChanged(String query) {
                presenter.loadFriends(query, view);
            }

            @Override
            public void onTagClicked(int userId) {
                if (presenter.isViewCanBeDeleted(userId)) ((ExistsTagView) view).showDeleteButton();
            }

            @Override
            public void onTagAdded(PhotoTag tag) {
                presenter.addPhotoTag(tag);
                addTagView(tag);
                removeView(view);
            }

            @Override
            public void onTagDeleted(PhotoTag tag) {
                presenter.deletePhotoTag(tag);
            }
        });

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        Size tagSize = view.getSize();
        float tagWidth = tagSize.getWidth();
        float tagHeight = tagSize.getHeight();

        int photoTagXPos = (int) (photoTag.getPosition().getTopLeft().getX() - tagWidth / 2);
        int photoTagYPos = (int) (photoTag.getPosition().getTopLeft().getY());

        if (photoTagXPos < 0) {
            photoTagXPos = 0;
        }
        if (photoTagXPos > getWidth() - tagWidth) {
            photoTagXPos = (int) (getWidth() - tagWidth);
        }
        if (photoTagYPos < 0) {
            photoTagYPos = 0;
        }
        if (photoTagYPos > getHeight() - tagHeight) {
            photoTagYPos = (int) (getHeight() - tagHeight - (getHeight() - photoTagYPos));
        }

        layoutParams.leftMargin = photoTagXPos;
        layoutParams.topMargin = photoTagYPos;

        addView(view, layoutParams);
    }

    @Override
    public void setupTags(List<PhotoTag> tags) {
        Queryable.from(tags).forEachR(tag -> {
            PhotoTag cloneTag = SerializationUtils.clone(tag);
            PhotoTag.TagPosition newTagPosition = CoordinatesTransformer.convertToAbsolute(cloneTag.getPosition(), imageBounds);
            cloneTag.setTagPosition(newTagPosition);
            addTagView(cloneTag);
        });
    }

    @Override
    public RectF getImageBounds() {
        return imageBounds;
    }

    @Override
    public void onRequestsComplete() {
        if (completeListener != null)
            completeListener.onTagComplete();
    }

    private void removeUncompletedViews() {
        View view = getChildAt(getChildCount() - 1);
        if (view instanceof NewTagView) removeView(view);
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            boolean confirmed = imageBounds.contains(event.getX(), event.getY())
                    && presenter.isOwnPhoto() && presenter.canAddTags();
            if (confirmed) {
                removeUncompletedViews();
                addTagView(event.getX(), event.getY());
            }
            return confirmed;
        }
    }

    public interface TagListener {

        void onQueryChanged(String query);

        void onTagClicked(int userId);

        void onTagAdded(PhotoTag tag);

        void onTagDeleted(PhotoTag tag);
    }

    public interface TaggableCompleteListener {

        void onTagComplete();
    }

    @Override
    public void informUser(int stringId) {

    }

    @Override
    public void informUser(String string) {

    }

    @Override
    public void alert(String s) {

    }

    @Override
    public boolean isVisibleOnScreen() {
        return false;
    }

    @Override
    public boolean isTabletLandscape() {
        return false;
    }
}
