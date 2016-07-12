package com.worldventures.dreamtrips.modules.dtl_flow;

import com.worldventures.dreamtrips.core.flow.path.FullScreenPath;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;

public abstract class DtlMasterPath extends DtlPath implements FullScreenPath {

    @Override
    public MasterDetailPath getMaster() {
        return this;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public boolean shouldHideDrawer() {
        return false;
    }
}
