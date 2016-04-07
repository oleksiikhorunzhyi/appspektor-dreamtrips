package com.worldventures.dreamtrips.modules.dtl_flow;

import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;

public abstract class DtlDetailPath extends DtlPath {

    private MasterDetailPath master;

    public DtlDetailPath(MasterDetailPath master) {
        this.master = master;
    }

    @Override
    public MasterDetailPath getMaster() {
        return master;
    }
}
