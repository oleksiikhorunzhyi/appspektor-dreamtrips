package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

public class PlacesUpdatedEvent {

    private DtlMerchantType type;

    public PlacesUpdatedEvent(DtlMerchantType type) {
        this.type = type;
    }

    public DtlMerchantType getType() {
        return type;
    }

    public void setType(DtlMerchantType type) {
        this.type = type;
    }
}
