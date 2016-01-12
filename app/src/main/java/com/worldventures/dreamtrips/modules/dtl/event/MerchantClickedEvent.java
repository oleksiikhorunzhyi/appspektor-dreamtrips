package com.worldventures.dreamtrips.modules.dtl.event;

public class MerchantClickedEvent {

    private final String merchantId;

    public MerchantClickedEvent(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantId() {
        return merchantId;
    }
}
