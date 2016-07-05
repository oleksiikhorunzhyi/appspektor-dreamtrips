package com.worldventures.dreamtrips.modules.profile.view.widgets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.R;

public class SwipeRefreshLayoutWithText extends SwipeRefreshLayout {

    private final static long DEFAULT_TIMEOUT = 1000;
    private final static int VERTICAL_OFFSET = 20;

    private TextView infoTextView;

    private long timeout;

    private WeakHandler weakHandler;

    public SwipeRefreshLayoutWithText(Context context) {
        this(context, null);
    }

    public SwipeRefreshLayoutWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        weakHandler = new WeakHandler();
        this.timeout = DEFAULT_TIMEOUT;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setInfoText(@StringRes int resource) {
        if (!(getChildAt(1) instanceof ViewGroup))
            throw new IllegalStateException("Child must be instance of ViewGroup");
        //
        if (infoTextView == null) {
            infoTextView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh_info,
                    null, false);
            ((ViewGroup) getChildAt(1)).addView(infoTextView);
            infoTextView.setText(resource);
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
        if (infoTextView != null && infoTextView.isAttachedToWindow() && !refreshing) {
            infoTextView.setVisibility(View.GONE);
        }
    }

    public void setRefreshing(boolean refreshing, boolean showInfoText) {
        super.setRefreshing(refreshing);
        if (infoTextView != null && infoTextView.isAttachedToWindow() && refreshing && showInfoText) {
            weakHandler.postDelayed(this::showInfoText, timeout);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getCircleImageView() != null) {
            final int width = getMeasuredWidth();
            final int infoWidth = infoTextView.getMeasuredWidth();
            final int infoHeight = infoTextView.getMeasuredHeight();
            infoTextView.layout((width / 2 - infoWidth / 2), getCurrentTargetOffsetTop() + getCircleHeight() + VERTICAL_OFFSET,
                    (width / 2 + infoWidth / 2), getCurrentTargetOffsetTop() + getCircleHeight() + infoHeight + VERTICAL_OFFSET);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        infoTextView.measure(0, 0);
    }

    private void showInfoText() {
        if (isRefreshing()) {
            infoTextView.setVisibility(View.VISIBLE);
        }
    }

    private int getCurrentTargetOffsetTop() {
        ImageView view = getCircleImageView();
        return view != null ? view.getTop() : 0;
    }

    private int getCircleHeight() {
        ImageView view = getCircleImageView();
        return view != null ? view.getHeight() : 0;
    }

    private ImageView getCircleImageView() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ImageView) {
                return (ImageView) getChildAt(i);
            }
        }
        return null;
    }
}
