package com.messenger.ui.util.chat.anim;

import android.animation.ValueAnimator;
import android.view.View;

public class SlideUpAnimator extends TimestampAnimator {

    public SlideUpAnimator(int position) {
        super(position);
    }

    @Override
    public void start() {
        dateTextView.setVisibility(View.VISIBLE);

        if (dateTextView.getMeasuredHeight() == 0) {
            dateTextView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }

        ValueAnimator animator = ValueAnimator.ofFloat(-dateTextView.getMeasuredHeight(), 0);
        animator.addUpdateListener(new BottomMarginAnimationListener(dateTextView));
        animator.start();
    }
}
