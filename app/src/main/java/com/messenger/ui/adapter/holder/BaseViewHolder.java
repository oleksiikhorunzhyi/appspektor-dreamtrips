package com.messenger.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        itemView.addOnAttachStateChangeListener(lifeCycleListener);
    }

    private View.OnAttachStateChangeListener lifeCycleListener = new
            View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    onAttachedToWindow();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    onDetachedFromWindow();
                }
            };

    protected void onAttachedToWindow() {
    }

    protected void onDetachedFromWindow() {
    }
}
