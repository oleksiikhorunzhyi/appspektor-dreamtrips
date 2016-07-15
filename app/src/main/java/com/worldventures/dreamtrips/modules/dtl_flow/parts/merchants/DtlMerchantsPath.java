package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;

@Layout(R.layout.screen_dtl_merchants)
public class DtlMerchantsPath extends DtlMasterPath {

    private final boolean allowRedirect;

    protected DtlMerchantsPath(boolean allowRedirect) {
        this.allowRedirect = allowRedirect;
    }

    protected DtlMerchantsPath() {
        this.allowRedirect = false;
    }

    public static DtlMerchantsPath getDefault() {
        return new DtlMerchantsPath();
    }

    public static DtlMerchantsPath withAllowedRedirection() {
        return new DtlMerchantsPath(true);
    }

    public boolean isAllowRedirect() {
        return allowRedirect;
    }
}
