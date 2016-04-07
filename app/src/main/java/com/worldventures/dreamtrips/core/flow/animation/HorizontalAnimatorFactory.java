package com.worldventures.dreamtrips.core.flow.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

public class HorizontalAnimatorFactory extends DirectionalAnimatorFactory {

    @Override protected Animator getForwardAnimator(View from, View to, ViewGroup container) {
        Animator animatorFrom = ObjectAnimator.ofFloat(from, View.TRANSLATION_X, -from.getWidth());
        Animator animatorTo   = ObjectAnimator.ofFloat(to, View.TRANSLATION_X, to.getWidth(), 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorFrom, animatorTo);
        return set;
    }

    @Override protected Animator getBackwardAnimator(View from, View to, ViewGroup container) {
        Animator animatorFrom = ObjectAnimator.ofFloat(from, View.TRANSLATION_X, from.getWidth());
        Animator animatorTo   = ObjectAnimator.ofFloat(to, View.TRANSLATION_X, -to.getWidth(), 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorFrom, animatorTo);
        return set;
    }

    @Override protected Animator getReplaceAnimator(View from, View to, ViewGroup container) {
        Animator alpha = ObjectAnimator.ofFloat(to, View.ALPHA, 0.75f, 1f);
        Animator transition =
                ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, container.getHeight() / 5, 0f);

        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(alpha, transition);
        animator.setDuration(container.getResources()
                .getInteger(android.R.integer.config_shortAnimTime));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        return animator;
    }
}
