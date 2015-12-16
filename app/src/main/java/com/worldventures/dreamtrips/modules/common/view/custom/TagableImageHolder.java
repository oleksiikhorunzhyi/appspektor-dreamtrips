package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.TaggableImageHolderPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.NewTagView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;

import java.util.List;

public class TagableImageHolder extends RelativeLayout implements TaggableImageHolderPresenter.View {

    private TaggableImageHolderPresenter presenter;
    private boolean setuped;

    public TagableImageHolder(Context context) {
        super(context);
    }

    public TagableImageHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagableImageHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagableImageHolder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setup(Injector injector, Photo photo, boolean isOwnPhoto) {
        presenter = TaggableImageHolderPresenter.create(photo, isOwnPhoto);
        injector.inject(presenter);
        presenter.takeView(this);
        presenter.onStart();
        setuped = true;
    }

    public boolean isSetuped() {
        return setuped;
    }

    public void hide() {
        presenter.pushRequests();
        //
        setVisibility(View.GONE);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.onStop();
        presenter.dropView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (presenter.isOwnPhoto())
                    addTagView(null, event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void addTagView(User user, float x, float y) {
        TagView view = new NewTagView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        view.setTag(user);
        view.setTagListener(new TagListener() {

            @Override
            public boolean onTagClicked(int userId) {
                return presenter.isViewCanBeDeleted(userId);
            }

            @Override
            public void onTagAdded(PhotoTag tag) {
                presenter.addPhotoTag(tag);
            }

            @Override
            public void onTagDeleted(PhotoTag tag) {
                presenter.deletePhotoTag(tag);
            }
        });

        float tagWidth = view.getTagWidthInPx();
        float tagHeight = view.getTagHeightInPx();

        int marginLeft = (int) (x - tagWidth / 2);
        int marginTop = (int) (y - tagHeight / 2);

        if (marginLeft < 0) {
            marginLeft = 0;
        }
        if (marginLeft > getWidth() - tagWidth) {
            marginLeft = 0;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        if (marginTop < 0) {
            marginTop = 0;
        }
        if (marginTop > getHeight() - tagHeight) {
            marginTop = 0;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        layoutParams.leftMargin = marginLeft;
        layoutParams.topMargin = marginTop;
        view.setLayoutParams(layoutParams);
        addView(view, layoutParams);
    }

    @Override
    public void setupTags(List<PhotoTag> tags) {
        Queryable.from(tags).forEachR(tag -> addTagView(tag.getUser(),
                tag.getPosition().getTopLeft().getX(), tag.getPosition().getTopLeft().getY()));
    }

    public interface TagListener {

        /**
        @return True if view can be deleted
         */
        boolean onTagClicked(int userId);

        void onTagAdded(PhotoTag tag);

        void onTagDeleted(PhotoTag tag);
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
