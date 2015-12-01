package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;

import java.util.Date;

public class EstimatePointsRequest extends DtlRequest<Double> {

    private final String id;
    private final double price;

    public EstimatePointsRequest(String id, double price) {
        super(Double.class);
        this.id = id;
        this.price = price;
    }

    @Override
    public Double loadDataFromNetwork() {
        return getService().estimatePoints(id, price,
                DateTimeUtils.convertDateToUTCString(new Date(System.currentTimeMillis()))).getPoints();
    }
}
