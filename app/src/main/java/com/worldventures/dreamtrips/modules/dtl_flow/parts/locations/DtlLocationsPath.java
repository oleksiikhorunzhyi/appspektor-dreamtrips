package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;

/**
 * Path class for screen of selecting location. Constructor is hidden, use {@link Builder} class
 * <br />
 * or static {@link DtlLocationsPath#getDefault()} method.
 * Params are {@link DtlLocationsPath#showNoMerchantsCaption} and {@link DtlLocationsPath#allowUserGoBack}
 * - both false by default.
 */
@Layout(R.layout.screen_dtl_locations)
public class DtlLocationsPath extends DtlMasterPath {

    private final boolean showNoMerchantsCaption;
    private final boolean allowUserGoBack;

    public static Builder builder() {
        return new Builder();
    }

    public static DtlLocationsPath getDefault() {
        return new Builder().build();
    }

    /**
     * Constructor is hidden, use generated Builder class
     */
    protected DtlLocationsPath(boolean showNoMerchantsCaption, boolean allowUserGoBack) {
        this.showNoMerchantsCaption = showNoMerchantsCaption;
        this.allowUserGoBack = allowUserGoBack;
    }

    public boolean isShowNoMerchantsCaption() {
        return showNoMerchantsCaption;
    }

    public boolean isAllowUserGoBack() {
        return allowUserGoBack;
    }

    @Override
    public PathAttrs getAttrs() {
        return allowUserGoBack ? WITH_DRAWER : WITHOUT_DRAWER;
    }

    public static class Builder {

        private boolean showNoMerchantsCaption = false;
        private boolean allowUserGoBack = false;

        public Builder showNoMerchantsCaption(boolean showNoMerchantsCaption) {
            this.showNoMerchantsCaption = showNoMerchantsCaption;
            return this;
        }

        public Builder allowUserGoBack(boolean allowUserGoBack) {
            this.allowUserGoBack = allowUserGoBack;
            return this;
        }

        public DtlLocationsPath build() {
            return new DtlLocationsPath(Builder.this.showNoMerchantsCaption,
                    Builder.this.allowUserGoBack);
        }
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
