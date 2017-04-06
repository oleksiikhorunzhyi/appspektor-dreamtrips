package com.worldventures.dreamtrips.api.trip.model;

import java.util.Date;
import java.util.List;

import static com.worldventures.dreamtrips.util.DateTimeUtils.DEFAULT_ISO_FORMAT_DATE_ONLY;
import static com.worldventures.dreamtrips.util.DateTimeUtils.convertDateToString;

public class TripParamsAdapter {

    private TripParams params;

    public TripParamsAdapter(TripParams params) {
        this.params = params;
    }

    public String query() {
        return params.query();
    }

    public Integer durationMin() {
        return params.durationMin();
    }

    public Integer durationMax() {
        return params.durationMax();
    }

    public Double priceMin() {
        return params.priceMin();
    }

    public Double priceMax() {
        return params.priceMax();
    }

    public String startDate() {
        return convertDateToQueryParam(params.startDate());
    }

    public String endDate() {
        return convertDateToQueryParam(params.endDate());
    }

    public String regions() {
        return convertListToQueryParam(params.regions());
    }

    public String activities() {
        return convertListToQueryParam(params.activities());
    }

    public Integer soldOut() {
        return convertBooleanToIntQueryParam(params.soldOut());
    }

    public Integer liked() {
        return convertBooleanToIntQueryParam(params.liked());
    }

    public Integer recentFirst() {
        return convertBooleanToIntQueryParam(params.recentFirst());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Query formatting rules
    ///////////////////////////////////////////////////////////////////////////

    private String convertDateToQueryParam(Date date) {
        if (date == null) return null;
        else return convertDateToString(DEFAULT_ISO_FORMAT_DATE_ONLY, date);
    }

    private String convertListToQueryParam(List value) {
        if (value == null) return null;
        else if (value.isEmpty()) return "";
        //
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.size(); i++) {
            builder.append(value.get(i));
            if (i < value.size() - 1) builder.append(',');
        }
        return builder.toString();
    }

    private Integer convertBooleanToIntQueryParam(Boolean value) {
        return value == null ? null : value ? 1 : 0;
    }
}
