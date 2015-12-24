package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public abstract class TagView extends RelativeLayout {
    protected List<User> userFriends;
    protected PhotoTag photoTag;
    protected TagListener tagListener;
    protected User account;
    protected Photo photo;

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

    public void setPhotoTag(PhotoTag photoTag) {
        this.photoTag = photoTag;
    }

    public void setAccount(User user) {
        this.account = user;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
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

    public static TagView create(Context context, PhotoTag photoTag, User account, Photo photo) {
        TagView tagView;
        if (photoTag.getUser() == null) {
            tagView = new NewTagView(context);
        } else {
            tagView = new ExistsTagView(context);
        }

        tagView.setPhotoTag(photoTag);
        tagView.setAccount(account);
        tagView.setPhoto(photo);
        return tagView;
    }

    public interface TagListener {

        void onQueryChanged(String query);

        void onTagAdded(PhotoTag tag);

        void onTagDeleted(PhotoTag tag);
    }
}