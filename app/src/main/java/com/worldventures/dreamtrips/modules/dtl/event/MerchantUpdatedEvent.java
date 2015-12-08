package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

public class MerchantUpdatedEvent {

    private DtlMerchantType type;

    public MerchantUpdatedEvent(DtlMerchantType type) {
        this.type = type;
    }

    public DtlMerchantType getType() {
        return type;
    }

    public void setType(DtlMerchantType type) {
        this.type = type;
    }
}
