package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import flow.path.Path;

@Layout(R.layout.screen_dtl_merchants)
public class DtlMerchantsPath extends DtlMasterPath {

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITH_DRAWER;
    }

    @Override
    public Path getEmpty() {
        return new DtlMapPath(this);
    }

    @Override
    public Path getSuperMasterPath() {
        return MasterToolbarPath.INSTANCE;
    }
}
