package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.TagableImageHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class TagView extends RelativeLayout{
    protected User user;
    protected List<User> userFriends;
    protected Point leftTopPoint;
    protected Point tagCenter;
    protected Point rightBottom;
    protected TagableImageHolder.TagListener tagListener;

    public void setTaggedUser(@Nullable User user) {
        this.user = user;
    }

    public void setUserFriends(@Nullable List<User> userFriends) {
        this.userFriends = (userFriends == null) ? new ArrayList<User>() : userFriends;
    }

    public void setTagCoordinates(Point leftTop, Point tagCenter, Point rightBottom) {
        this.leftTopPoint = leftTop;
        this.tagCenter = tagCenter;
        this.rightBottom = rightBottom;
    }

    public void setTagListener(TagableImageHolder.TagListener tagListener) {
        this.tagListener = tagListener;
    }

    //region Constructors
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
    //endregion

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public float getTagWidthInPx(){
        return getResources().getDimension(R.dimen.tag_common_width);
    }

    public float getTagHeightInPx(){
        return getResources().getDimension(R.dimen.tag_common_height);
    }

    protected abstract void initialize();

    protected void deleteTag(){
        ((ViewGroup) getParent()).removeView(this);
    }
}