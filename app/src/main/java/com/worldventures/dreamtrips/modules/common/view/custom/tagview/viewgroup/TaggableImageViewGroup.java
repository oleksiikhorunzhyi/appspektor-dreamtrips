package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.presenter.TaggableImageHolderPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.CreationTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.ExistsTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import icepick.Icepick;
import icepick.State;

public abstract class TaggableImageViewGroup<P extends TaggableImageHolderPresenter> extends RelativeLayout implements TaggableImageHolderPresenter.View {

    protected P presenter;
    private boolean setuped;

    @State
    boolean isShown;

    protected RectF imageBounds = new RectF();
    protected PhotoTaggableHolderViewDelegate delegate;

    public TaggableImageViewGroup(Context context) {
        this(context, null);
    }

    public TaggableImageViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaggableImageViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        delegate = new PhotoTaggableHolderViewDelegate(this);
    }

    public void setup(Injector injector, Photo photo) {
        presenter = createPresenter(photo);
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
    protected abstract P createPresenter(Photo photo);

    public boolean isSetuped() {
        return setuped;
    }

    public void hide() {
        removeUncompletedViews();
        //
        setVisibility(View.INVISIBLE);
        isShown = false;
    }

    public void show(SimpleDraweeView imageView) {
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        imageView.getHierarchy().getActualImageBounds(imageBounds);
        setVisibility(View.VISIBLE);
        isShown = true;
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    public void redrawTags() {
        removeAllViews();
        presenter.showExistingTags();
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

    protected void addExistsTagView(PhotoTag photoTag) {
        TagView view = new ExistsTagView(getContext());
        addTagView(view, photoTag);
    }

    protected <T extends TagView> void addTagView(T view, PhotoTag photoTag) {
        addTagView(view, photoTag, -1);
    }

    protected <T extends TagView> void addTagView(T view, PhotoTag photoTag, int viewPos) {
        TagPosition tagPosition = CoordinatesTransformer.convertToAbsolute(photoTag.getProportionalPosition(), getImageBounds());
        view.setAbsoluteTagPosition(tagPosition);

        view.setPhotoTag(photoTag);
        view.setPhoto(presenter.getPhoto());
        LayoutParams layoutParams = calculatePosition(view);
        addView(view, viewPos, layoutParams);
    }

    @NonNull
    private LayoutParams calculatePosition(TagView view) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        Size tagSize = view.getSize();
        float tagWidth = tagSize.getWidth();
        float tagHeight = tagSize.getHeight();
        int photoTagXPos = (int) (view.getAbsoluteTagPosition().getBottomRight().getX() - tagWidth / 2);
        int photoTagYPos = (int) (view.getAbsoluteTagPosition().getBottomRight().getY());

        if (photoTagXPos < 0) {
            photoTagXPos = 0;
        }
        if (view.getAbsoluteTagPosition().getTopLeft().getX() > getWidth() - tagWidth) {
            photoTagXPos = (int) (getWidth() - tagWidth);
        }
        if (photoTagYPos < 0) {
            photoTagYPos = 0;
        }
        if (view.getAbsoluteTagPosition().getTopLeft().getY() > getHeight() - tagHeight) {
            photoTagYPos = (int) (getHeight() - tagHeight - (getHeight() - photoTagYPos));
        }

        layoutParams.leftMargin = photoTagXPos;
        layoutParams.topMargin = photoTagYPos;
        return layoutParams;
    }

    @Override
    public void addTags(List<PhotoTag> tags) {
        Queryable.from(tags)
                .filter((photoTag) -> !delegate.isExistingViewExist(photoTag))
                .forEachR(this::addExistsTagView);
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
