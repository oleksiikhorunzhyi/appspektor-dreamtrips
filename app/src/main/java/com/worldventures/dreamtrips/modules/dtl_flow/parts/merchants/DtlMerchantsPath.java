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

    private final boolean fromLocationScreen;

    public static Builder builder() {
        return new Builder();
    }

    public static DtlMerchantsPath getDefault() {
        return new Builder().build();
    }

    /**
     * Constructor is hidden, use generated Builder class
     */
    protected DtlMerchantsPath(boolean fromLocationScreen) {
        this.fromLocationScreen = fromLocationScreen;
    }

    public boolean isfromLocationScreen() {
        return fromLocationScreen;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITH_DRAWER;
    }

    @Override
    public Path getEmpty() {
        return new DtlMapPath(this);
    }

    @Override
    public Path getMasterToolbarPath() {
        return MasterToolbarPath.INSTANCE;
    }

    public static class Builder {

        private boolean fromLocationScreen = false;

        public Builder fromLocationScreen(boolean fromLocationScreen) {
            this.fromLocationScreen = fromLocationScreen;
            return this;
        }

        public DtlMerchantsPath build() {
            return new DtlMerchantsPath(Builder.this.fromLocationScreen);
        }
    }
}
