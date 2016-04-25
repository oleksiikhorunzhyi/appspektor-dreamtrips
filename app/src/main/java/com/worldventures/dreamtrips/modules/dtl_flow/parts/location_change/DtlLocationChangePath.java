package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;

@Layout(R.layout.screen_dtl_location_change)
public class DtlLocationChangePath extends DtlMasterPath {

    @Override
    public PathAttrs getAttrs() {
        return WITHOUT_DRAWER;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }
}
