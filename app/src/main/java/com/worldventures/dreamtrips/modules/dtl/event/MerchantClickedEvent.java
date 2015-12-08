package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class MerchantClickedEvent {

    private final DtlMerchant dtlMerchant;

    public MerchantClickedEvent(DtlMerchant dtlMerchant) {
        this.dtlMerchant = dtlMerchant;
    }

    public DtlMerchant getDtlMerchant() {
        return dtlMerchant;
    }
}
