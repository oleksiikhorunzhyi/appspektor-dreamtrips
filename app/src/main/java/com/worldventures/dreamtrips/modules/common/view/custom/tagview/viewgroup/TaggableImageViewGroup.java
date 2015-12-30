package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.presenter.TaggableImageHolderPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.CreationTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagActionListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagCreationActionsListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

import icepick.Icepick;
import icepick.State;

public abstract class TaggableImageViewGroup<P extends TaggableImageHolderPresenter> extends RelativeLayout implements TaggableImageHolderPresenter.View {

    protected P presenter;
    private boolean setuped;

    @State
    boolean isShown;

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
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        return Icepick.saveInstanceState(this, parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
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
        isShown = false;
    }

    public void show(RectF imageBounds) {
        this.imageBounds = imageBounds;
        setVisibility(View.VISIBLE);
        presenter.setupTags();
        isShown = true;
    }

    @Override
    public boolean isShown() {
        return isShown;
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
        TagView view = TagView.create(getContext(), photoTag, presenter.getAccount(), presenter.getPhoto());
        PhotoTag.TagPosition tagPosition = CoordinatesTransformer.convertToAbsolute(photoTag.getPosition(), getImageBounds());
        view.setAbsoluteTagPosition(tagPosition);
        view.setTagListener(createTagListener(view));
        LayoutParams layoutParams = calculatePosition(view);
        addView(view, layoutParams);
    }

    @NonNull
    protected TagActionListener createTagListener(final TagView view) {
        return presenter::deletePhotoTag;
    }

    @NonNull
    private LayoutParams calculatePosition(TagView view) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        Size tagSize = view.getSize();
        float tagWidth = tagSize.getWidth();
        float tagHeight = tagSize.getHeight();
        int photoTagXPos = (int) (view.getAbsoluteTagPosition().getTopLeft().getX() - tagWidth / 2);
        int photoTagYPos = (int) (view.getAbsoluteTagPosition().getTopLeft().getY());

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
        Queryable.from(tags).forEachR(this::addTagView);
    }

    @Override
    public RectF getImageBounds() {
        return imageBounds;
    }

    protected void removeUncompletedViews() {
        View view = getChildAt(getChildCount() - 1);
        if (view instanceof CreationTagView) removeView(view);
    }

    @Override
    public void informUser(int stringId) {

    }

    @Override
    public void informUser(String message) {

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
