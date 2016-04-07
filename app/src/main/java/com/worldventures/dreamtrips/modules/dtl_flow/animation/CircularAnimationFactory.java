package com.worldventures.dreamtrips.modules.dtl_flow.animation;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.core.flow.animation.AnimatorFactory;

import flow.Flow;

public class CircularAnimationFactory implements AnimatorFactory {

    @Override
    public Animator createAnimator(View from, View to, Flow.Direction direction, ViewGroup container) {
        return ViewAnimationUtils.createCircularReveal(
                to,
                from.getWidth() / 2,
                from.getHeight() / 2,
                0F,
                Math.max(to.getWidth(), to.getHeight())
        );
    }
}
