package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.StyledPath;

@Layout(R.layout.screen_dtl_locations)
public class DtlLocationsPath extends StyledPath {

    private final boolean showNoMerchantsCaption;

    public DtlLocationsPath() {
        showNoMerchantsCaption = false;
    }

    public DtlLocationsPath(boolean showNoMerchantsCaption) {
        this.showNoMerchantsCaption = showNoMerchantsCaption;
    }

    public boolean isShowNoMerchantsCaption() {
        return showNoMerchantsCaption;
    }

    @Override
    public PathAttrs getAttrs() {
        return WITH_DRAWER;
    }
}
