package com.worldventures.dreamtrips.modules.dtl.event;

public class PlaceClickedEvent {

    private final String merchantId;

    public PlaceClickedEvent(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantId() {
        return merchantId;
    }
}
