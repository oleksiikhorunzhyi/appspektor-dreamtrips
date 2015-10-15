package com.worldventures.dreamtrips.modules.dtl.api;

public class RatePlaceRequest extends DtlRequest<Void> {

    private int id;
    private int stars;

    public RatePlaceRequest(int id, int stars) {
        super(Void.class);
        this.id = id;
        this.stars = stars;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().rate(id, stars);
    }
}
