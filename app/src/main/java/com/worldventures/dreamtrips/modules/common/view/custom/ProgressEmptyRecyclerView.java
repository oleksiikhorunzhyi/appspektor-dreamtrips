package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ProgressEmptyRecyclerView extends EmptyRecyclerView {

    View progressView;

    public ProgressEmptyRecyclerView(Context context) {
        super(context);
    }

    public ProgressEmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressEmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void showProgress() {
        if (progressView != null) {
            progressView.setVisibility(VISIBLE);
            setVisibility(GONE);
        }
    }

    public void hideProgress() {
        if (progressView != null) {
            progressView.setVisibility(GONE);
            setVisibility(VISIBLE);
        }
    }

    public void setProgressView(View progressView) {
        this.progressView = progressView;
        if (progressView != null) {
            progressView.setVisibility(GONE);
        }
    }
}
