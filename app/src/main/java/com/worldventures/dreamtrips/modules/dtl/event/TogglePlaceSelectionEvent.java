package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class TogglePlaceSelectionEvent {
    private DtlMerchant DtlMerchant;

    public TogglePlaceSelectionEvent(DtlMerchant DtlMerchant) {
        this.DtlMerchant = DtlMerchant;
    }

    public DtlMerchant getDtlMerchant() {
        return DtlMerchant;
    }
}
