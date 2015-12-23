package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
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

public abstract class TaggableImageViewGroup<P extends TaggableImageHolderPresenter> extends RelativeLayout implements TaggableImageHolderPresenter.View {

    protected P presenter;
    private boolean setuped;


    protected RectF imageBounds;

    public TaggableImageViewGroup(Context context) {
        super(context);
    }

    public TaggableImageViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaggableImageViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setup(Injector injector, Photo photo) {
        presenter = getPresenter(photo);
        injector.inject(presenter);
        presenter.takeView(this);
        presenter.onStart();
        setuped = true;
        //
    }

    @NonNull
    protected abstract P getPresenter(Photo photo);

    public boolean isSetuped() {
        return setuped;
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

    @Override
    protected void onDetachedFromWindow() {
        hide();
        if (presenter != null) {
            presenter.onStop();
            presenter.dropView();
        }
        super.onDetachedFromWindow();
    }

    protected void addTagView(PhotoTag photoTag) {
        TagView view = TagView.create(getContext(), photoTag);
        view.setTagListener(new TagView.TagListener() {

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

        LayoutParams layoutParams = calculatePosition(photoTag, view);
        addView(view, layoutParams);
    }

    @NonNull
    private LayoutParams calculatePosition(PhotoTag photoTag, TagView view) {
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
        return layoutParams;
    }

    @Override
    public void addTags(List<PhotoTag> tags) {
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


    protected void removeUncompletedViews() {
        View view = getChildAt(getChildCount() - 1);
        if (view instanceof NewTagView) removeView(view);
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
