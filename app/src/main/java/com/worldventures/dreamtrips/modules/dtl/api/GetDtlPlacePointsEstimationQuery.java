package com.worldventures.dreamtrips.modules.dtl.api;

public class GetDtlPlacePointsEstimationQuery extends DtlRequest<Double> {

    private int id;
    private double price;

    public GetDtlPlacePointsEstimationQuery(int id, double price) {
        super(Double.class);
        this.id = id;
        this.price = price;
    }

    @Override
    public Double loadDataFromNetwork() {
        return getService().getDtlPlacePointsEstimation(id, price).getPoints();
    }
}
