package com.worldventures.dreamtrips.core.flow.animation;

import android.support.v4.util.SimpleArrayMap;
import android.util.Pair;

import flow.path.Path;

public class BaseAnimatorRegistrar implements ScreenAnimatorRegistrar {

    protected static SimpleArrayMap<Pair<Class<? extends Path>,
            Class<? extends Path>>, AnimatorFactory> animators;

    public BaseAnimatorRegistrar() {
        animators = new SimpleArrayMap<>();
    }

    @Override
    public AnimatorFactory getAnimatorFactory(Path from, Path to){
        AnimatorFactory animatorFactory = animators.get(new Pair<>(from.getClass(), to.getClass()));
        return animatorFactory == null ? new HorizontalAnimatorFactory() : animatorFactory;
    }
}
