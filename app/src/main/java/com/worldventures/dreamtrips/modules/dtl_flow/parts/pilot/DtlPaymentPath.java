package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import flow.path.Path;

@Layout(R.layout.activity_thank_you_screen)
public class DtlPaymentPath extends DtlMasterPath {

    private final boolean isPaid;
    private final String totalAmount;
    private final String merchantName;

    public DtlPaymentPath(boolean isPaid, String totalAmount, String merchantName) {
        super();
        this.isPaid = isPaid;
        this.totalAmount = totalAmount;
        this.merchantName = merchantName;
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

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getMerchantName() {
        return merchantName;
    }
}
