package com.worldventures.dreamtrips.core.flow.animation;

import flow.path.Path;

public interface ScreenAnimatorRegistrar {

    AnimatorFactory getAnimatorFactory(Path from, Path to);
}
