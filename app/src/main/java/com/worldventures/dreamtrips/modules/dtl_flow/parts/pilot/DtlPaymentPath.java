package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;

import flow.path.Path;

@Layout(R.layout.activity_thank_you_screen)
public class DtlPaymentPath extends DtlMasterPath {

    private final boolean isPaid;

    public DtlPaymentPath(boolean isPaid) {
        super();
        this.isPaid = isPaid;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }

    @Override
    public Path getMasterToolbarPath() {
        return MasterToolbarPath.INSTANCE;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
