package com.worldventures.dreamtrips.core.flow.animation;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import flow.Flow;

public abstract class DirectionalAnimatorFactory implements AnimatorFactory {

    @Override public Animator createAnimator(View from, View to, Flow.Direction direction, ViewGroup container) {
        switch (direction){
            case FORWARD:
                return getForwardAnimator(from, to, container);
            case BACKWARD:
                return getBackwardAnimator(from, to, container);
            case REPLACE:
                return getReplaceAnimator(from, to, container);
            default:
                throw new IllegalArgumentException("Unknown direction");
        }
    }

    protected abstract Animator getForwardAnimator(View from, View to, ViewGroup container);

    protected abstract Animator getBackwardAnimator(View from, View to, ViewGroup container);

    protected abstract Animator getReplaceAnimator(View from, View to, ViewGroup container);
}
