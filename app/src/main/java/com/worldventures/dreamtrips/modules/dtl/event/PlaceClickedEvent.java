package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class PlaceClickedEvent {

    private final DtlMerchant place;

    public PlaceClickedEvent(DtlMerchant place) {
        this.place = place;
    }

    public DtlMerchant getPlace() {
        return place;
    }
}
