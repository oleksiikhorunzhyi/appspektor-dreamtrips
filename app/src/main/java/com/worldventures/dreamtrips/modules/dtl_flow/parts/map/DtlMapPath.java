package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;

@Layout(R.layout.screen_dtl_map)
public class DtlMapPath extends DtlDetailPath {

    public DtlMapPath(MasterDetailPath master) {
        super(master);
    }

    @Override
    public PathAttrs getAttrs() {
        return WITH_DRAWER;
    }
}
