package com.worldventures.dreamtrips.modules.dtl.model;

public class DtlTransactionLocation {
    private String city;
    private String state;
    private String country;
    private double latitude;
    private double longitude;

    public static DtlTransactionLocation fromDtlPlace(DtlPlace dtlPlace) {
        DtlTransactionLocation  dtlTransactionLocation = new DtlTransactionLocation();
        dtlTransactionLocation.city = dtlPlace.getCity();
        dtlTransactionLocation.state = dtlPlace.getState();
        dtlTransactionLocation.country = dtlPlace.getCountry();
        dtlTransactionLocation.latitude = 0.0d;
        dtlTransactionLocation.longitude = 0.0d;
        return dtlTransactionLocation;
    }
}
