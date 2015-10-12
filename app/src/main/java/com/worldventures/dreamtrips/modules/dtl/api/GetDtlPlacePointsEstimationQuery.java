package com.worldventures.dreamtrips.modules.dtl.api;

public class GetDtlPlacePointsEstimationQuery extends DtlRequest<Float> {

    private int id;
    private float price;

    public GetDtlPlacePointsEstimationQuery(int id, float price) {
        super(Float.class);
        this.id = id;
        this.price = price;
    }

    @Override
    public Float loadDataFromNetwork() {
        Float points = getService().getDtlPlacePointsEstimation(id, price);
        if (points == null) points = 0F;
        return points;
    }
}
