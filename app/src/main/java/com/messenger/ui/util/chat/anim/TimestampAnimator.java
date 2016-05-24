package com.messenger.ui.util.chat.anim;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class TimestampAnimator {

    protected int position;
    protected TextView dateTextView;

    public TimestampAnimator(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setDateTextView(TextView dateTextView) {
        this.dateTextView = dateTextView;
    }

    public abstract void start();

    protected class BottomMarginAnimationListener implements ValueAnimator.AnimatorUpdateListener {

        private View view;

        public BottomMarginAnimationListener(View view) {
            this.view = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            float margin = (Float) animator.getAnimatedValue();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = (int) margin;
            view.requestLayout();
        }
    }
}
