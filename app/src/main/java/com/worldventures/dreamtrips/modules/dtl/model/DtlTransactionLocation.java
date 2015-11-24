package com.worldventures.dreamtrips.modules.dtl.model;

public class DtlTransactionLocation {
    private String city;
    private String state;
    private String country;
    private double latitude;
    private double longitude;

    public static DtlTransactionLocation fromDtlPlace(DtlPlace dtlPlace, double latitude, double longitude) {
        DtlTransactionLocation  dtlTransactionLocation = new DtlTransactionLocation();
        dtlTransactionLocation.city = dtlPlace.getCity();
        dtlTransactionLocation.state = dtlPlace.getState();
        dtlTransactionLocation.country = dtlPlace.getCountry();
        dtlTransactionLocation.latitude = latitude;
        dtlTransactionLocation.longitude = longitude;
        return dtlTransactionLocation;
    }
}
