package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlMerchantsPresenter extends FlowPresenter<DtlMerchantsScreen, ViewState.EMPTY> {

    void applySearch(String query);

    void merchantClicked(DtlMerchant merchant);
}
