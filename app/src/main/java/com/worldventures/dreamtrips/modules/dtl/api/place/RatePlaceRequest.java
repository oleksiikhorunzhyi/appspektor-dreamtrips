package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;

public class RatePlaceRequest extends DtlRequest<Void> {

    private String id;
    private int stars;

    public RatePlaceRequest(String id, int stars) {
        super(Void.class);
        this.id = id;
        this.stars = stars;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().rate(id, stars);
    }
}
