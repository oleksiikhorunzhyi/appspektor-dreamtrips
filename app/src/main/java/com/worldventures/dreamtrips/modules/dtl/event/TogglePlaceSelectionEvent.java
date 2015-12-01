package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

public class TogglePlaceSelectionEvent {
    private DTlMerchant DTlMerchant;

    public TogglePlaceSelectionEvent(DTlMerchant DTlMerchant) {
        this.DTlMerchant = DTlMerchant;
    }

    public DTlMerchant getDTlMerchant() {
        return DTlMerchant;
    }
}
