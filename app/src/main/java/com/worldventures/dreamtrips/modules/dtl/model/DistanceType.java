package com.worldventures.dreamtrips.modules.dtl.model;

public enum DistanceType {
    MILES("ml"), KMS("km");

    String analyticsTypeName;

    DistanceType(String analyticsTypeName) {
        this.analyticsTypeName = analyticsTypeName;
    }

    public String getTypeNameForAnalytics() {
        return analyticsTypeName;
    }
}
