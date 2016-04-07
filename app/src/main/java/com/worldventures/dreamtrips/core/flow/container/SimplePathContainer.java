/*
 * Copyright 2014 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldventures.dreamtrips.core.flow.container;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.flow.animation.AnimatorFactory;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.flow.util.Utils;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.animation.DtlAnimatorRegistrar;

import java.util.LinkedHashMap;
import java.util.Map;

import flow.Flow;
import flow.Flow.Direction;
import flow.path.Path;
import flow.path.PathContainer;
import flow.path.PathContext;
import flow.path.PathContextFactory;

/**
 * Provides basic right-to-left transitions. Saves and restores view state.
 * Uses {@link PathContext} to allow customized sub-containers.
 */
public class SimplePathContainer extends PathContainer {
    private static final Map<Class, Integer> PATH_LAYOUT_CACHE = new LinkedHashMap<>();
    private final PathContextFactory contextFactory;
    private Context context;
    private DtlAnimatorRegistrar animatorRegistrar = new DtlAnimatorRegistrar();

    public SimplePathContainer(Context context, int tagKey, PathContextFactory contextFactory) {
        super(tagKey);
        this.context = context;
        this.contextFactory = contextFactory;
    }

    @Override
    protected void performTraversal(final ViewGroup containerView,
                                    final TraversalState traversalState, final Direction direction,
                                    final Flow.TraversalCallback callback) {

        final PathContext pathContext;
        final PathContext oldPath;
        if (containerView.getChildCount() > 0) {
            oldPath = PathContext.get(containerView.getChildAt(0).getContext());
        } else {
            oldPath = PathContext.root(containerView.getContext());
        }
        //
        Path to = traversalState.toPath();
        //
        View newView;
        pathContext = PathContext.create(oldPath, to, contextFactory);
        int layout = getLayout(to);
        newView =
                LayoutInflater.from(pathContext).cloneInContext(pathContext).inflate(layout, containerView, false);
        //
        if (newView instanceof InjectorHolder
                && context instanceof Injector) {
            ((InjectorHolder) newView).setInjector((Injector) context);
        }
        //
        if (newView instanceof PathView)
            ((PathView) newView).setPath(to);
        //
        final View fromView;
        if (traversalState.fromPath() != null) {
            fromView = containerView.getChildAt(0);
            traversalState.saveViewState(fromView);
        } else fromView = null;
        traversalState.restoreViewState(newView);
        //
        if (fromView == null) {
            containerView.removeAllViews();
            containerView.addView(newView);
            oldPath.destroyNotIn(pathContext, contextFactory);
            callback.onTraversalCompleted();
            return;
        }
        //
        containerView.addView(newView);
        Utils.waitForMeasure(newView, ((view, width, height) -> {
            if (animatorRegistrar == null) {
                containerView.removeView(fromView);
                oldPath.destroyNotIn(pathContext, contextFactory);
                callback.onTraversalCompleted();
                return;
            }
            //
            AnimatorFactory factory = animatorRegistrar.getAnimatorFactory(traversalState.fromPath(), to);
            Animator animator = factory.createAnimator(fromView, newView, direction, containerView);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    containerView.removeView(fromView);
                    oldPath.destroyNotIn(pathContext, contextFactory);
                    callback.onTraversalCompleted();
                }
            });
            animator.start();
        }));
    }

    protected int getLayout(Path path) {
        Class pathType = path.getClass();
        @LayoutRes Integer layoutResId = PATH_LAYOUT_CACHE.get(pathType);
        if (layoutResId == null) {
            layoutResId = FlowUtil.layoutFrom(pathType);
            PATH_LAYOUT_CACHE.put(pathType, layoutResId);
        }
        return layoutResId;
    }
}
