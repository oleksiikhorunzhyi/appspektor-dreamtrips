package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowScreen;

public interface DtlMapInfoScreen extends FlowScreen {

    void visibleLayout(boolean show);

    void setMerchant(DtlMerchant merchant);
}
