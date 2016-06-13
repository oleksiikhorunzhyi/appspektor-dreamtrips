package com.messenger.flow.path;

import com.worldventures.dreamtrips.core.flow.path.PathAttrs;

public abstract class StyledPath extends MasterDetailPath {

    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }
}
