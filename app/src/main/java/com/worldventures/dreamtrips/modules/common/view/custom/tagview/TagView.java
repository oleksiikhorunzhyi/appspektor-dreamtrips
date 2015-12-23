package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.TaggableImageViewGroup;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public abstract class TagView extends RelativeLayout {
    protected List<User> userFriends;
    protected PhotoTag photoTag;
    protected TagListener tagListener;

    @InjectView(R.id.pointer_top)
    View pointerTop;
    @InjectView(R.id.pointer_bottom)
    View pointerBottom;
    @InjectView(R.id.pointer_shift_x)
    View space;

    public TagView(Context context) {
        super(context);
        initialize();
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void setPhotoTag(PhotoTag photoTag) {
        this.photoTag = photoTag;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int leftMargin = ((LayoutParams) this.getLayoutParams()).leftMargin;
        int topMargin = ((LayoutParams) this.getLayoutParams()).topMargin;
        float tagPosition = photoTag.getPosition().getTopLeft().getX();
        pointerTop.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int width = pointerTop.getMeasuredWidth();
        space.getLayoutParams().width = (int) (tagPosition - leftMargin - (width / 2));

        int y = (int) photoTag.getPosition().getTopLeft().getY();
        if (y > topMargin + this.getHeight()) {
            pointerTop.setVisibility(GONE);
            pointerBottom.setVisibility(VISIBLE);
        } else {
            pointerTop.setVisibility(VISIBLE);
            pointerBottom.setVisibility(GONE);
        }
    }

    public void setUserFriends(@Nullable List<User> userFriends) {
        this.userFriends = (userFriends == null) ? new ArrayList<>() : userFriends;
    }

    public void setTagListener(TagListener tagListener) {
        this.tagListener = tagListener;
    }

    public Size getSize() {
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        return new Size(width, height);
    }

    protected abstract void initialize();

    protected void deleteTag() {
        ((ViewGroup) getParent()).removeView(this);
    }

    public static TagView create(Context context, PhotoTag photoTag) {
        TagView tagView;
        if (photoTag.getUser() == null) {
            tagView = new NewTagView(context);
        } else {
            tagView = new ExistsTagView(context);
        }

        tagView.setPhotoTag(photoTag);

        return tagView;
    }


    public interface TagListener {

        void onQueryChanged(String query);

        void onTagClicked(int userId);

        void onTagAdded(PhotoTag tag);

        void onTagDeleted(PhotoTag tag);
    }
}