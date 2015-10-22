package com.worldventures.dreamtrips.modules.dtl.api;

public class GetDtlPlacePointsEstimationQuery extends DtlRequest<Double> {

    private String id;
    private double price;

    public GetDtlPlacePointsEstimationQuery(String id, double price) {
        super(Double.class);
        this.id = id;
        this.price = price;
    }

    @Override
    public Double loadDataFromNetwork() {
        return getService().getDtlPlacePointsEstimation(id, price).getPoints();
    }
}
