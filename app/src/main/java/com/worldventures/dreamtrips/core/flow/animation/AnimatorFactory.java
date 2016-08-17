package com.worldventures.dreamtrips.core.flow.animation;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import flow.Flow;

public interface AnimatorFactory {

   Animator createAnimator(View from, View to, Flow.Direction direction, ViewGroup container);
}
