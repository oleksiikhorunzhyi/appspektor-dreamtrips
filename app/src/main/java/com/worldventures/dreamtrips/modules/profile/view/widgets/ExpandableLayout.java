package com.worldventures.dreamtrips.modules.profile.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.core.utils.AnimationUtils;

public class ExpandableLayout extends RelativeLayout {

    private FrameLayout contentLayout;
    private FrameLayout headerLayout;
    //
    private Integer duration;
    private Boolean isAnimationRunning = false;

    public ExpandableLayout(Context context) {
        super(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        final View rootView = View.inflate(context, com.andexert.expandablelayout.library.R.layout.view_expandable, this);
        headerLayout = (FrameLayout) rootView.findViewById(com.andexert.expandablelayout.library.R.id.view_expandable_headerlayout);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, com.andexert.expandablelayout.library.R.styleable.ExpandableLayout);
        final int headerID = typedArray.getResourceId(com.andexert.expandablelayout.library.R.styleable.ExpandableLayout_el_headerLayout, -1);
        final int contentID = typedArray.getResourceId(com.andexert.expandablelayout.library.R.styleable.ExpandableLayout_el_contentLayout, -1);
        contentLayout = (FrameLayout) rootView.findViewById(com.andexert.expandablelayout.library.R.id.view_expandable_contentLayout);

        if (headerID == -1 || contentID == -1)
            throw new IllegalArgumentException("HeaderLayout and ContentLayout cannot be null!");

        if (isInEditMode())
            return;

        duration = typedArray.getInt(com.andexert.expandablelayout.library.R.styleable.ExpandableLayout_el_duration, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        final View headerView = View.inflate(context, headerID, null);
        headerView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        headerLayout.addView(headerView);
        final View contentView = View.inflate(context, contentID, null);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        contentLayout.addView(contentView);
        contentLayout.setVisibility(GONE);
        headerLayout.setOnClickListener((view) -> {
            if (!isAnimationRunning) {
                if (contentLayout.getVisibility() == VISIBLE) {
                    collapse(contentLayout);
                } else {
                    expand(contentLayout);
                }

                isAnimationRunning = true;
                new Handler().postDelayed(() -> isAnimationRunning = false, duration);
            }
        });

        typedArray.recycle();
    }

    protected void expand(final View view) {
        measureView(view);
        //
        final Animation animation = AnimationUtils.provideExpandAnimation(view, duration);
        view.startAnimation(animation);
    }

    protected void collapse(final View view) {
        final Animation animation = AnimationUtils.provideCollapseAnimation(view, duration);
        view.startAnimation(animation);
    }

    private void measureView(View view) {
        int measureSpecWidth = MeasureSpec.makeMeasureSpec(this.getWidth(), MeasureSpec.AT_MOST);
        int measureSpecHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        //
        view.measure(measureSpecWidth, measureSpecHeight);
    }

    public Boolean isOpened() {
        return contentLayout.getVisibility() == VISIBLE;
    }

    public Integer getDuration() {
        return duration;
    }

    public Boolean isAnimationRunning() {
        return isAnimationRunning;
    }

    public void show() {
        if (!isAnimationRunning) {
            expand(contentLayout);
            isAnimationRunning = true;
            new Handler().postDelayed(() -> isAnimationRunning = false, duration);
        }
    }

    public FrameLayout getHeaderLayout() {
        return headerLayout;
    }

    public FrameLayout getContentLayout() {
        return contentLayout;
    }

    public void hide() {
        if (!isAnimationRunning) {
            collapse(contentLayout);
            isAnimationRunning = true;
            new Handler().postDelayed(() -> isAnimationRunning = false, duration);
        }
    }

    public void hideWithoutAnimation() {
        contentLayout.setVisibility(View.GONE);
    }

    public void showWithoutAnimation() {
        contentLayout.setVisibility(VISIBLE);
    }
}
