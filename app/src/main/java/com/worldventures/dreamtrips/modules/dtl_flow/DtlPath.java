package com.worldventures.dreamtrips.modules.dtl_flow;

import com.worldventures.dreamtrips.core.flow.path.AttributedPath;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;

public abstract class DtlPath extends MasterDetailPath implements AttributedPath {

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }
}
