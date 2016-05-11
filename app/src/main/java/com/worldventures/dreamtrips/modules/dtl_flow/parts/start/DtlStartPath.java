package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;

@Layout(R.layout.screen_dtl_start)
public class DtlStartPath extends DtlMasterPath {

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITH_DRAWER;
    }
}
