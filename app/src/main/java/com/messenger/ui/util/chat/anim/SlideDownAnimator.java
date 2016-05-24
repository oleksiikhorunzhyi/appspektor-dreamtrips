package com.messenger.ui.util.chat.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import com.messenger.ui.anim.SimpleAnimatorListener;

public class SlideDownAnimator extends TimestampAnimator {

    public SlideDownAnimator(int position) {
        super(position);
    }

    @Override
    public void start() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, -dateTextView.getMeasuredHeight());
        animator.addUpdateListener(new BottomMarginAnimationListener(dateTextView));
        animator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                dateTextView.setVisibility(View.GONE);
            }
        });
        animator.start();
    }
}
