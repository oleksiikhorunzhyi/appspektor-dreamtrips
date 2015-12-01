package com.worldventures.dreamtrips.modules.dtl.event;

import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

public class PlaceClickedEvent {

    private final DTlMerchant place;

    public PlaceClickedEvent(DTlMerchant place) {
        this.place = place;
    }

    public DTlMerchant getPlace() {
        return place;
    }
}
