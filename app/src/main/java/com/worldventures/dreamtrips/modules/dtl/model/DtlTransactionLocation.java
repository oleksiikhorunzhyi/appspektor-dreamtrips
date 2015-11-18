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
        dtlTransactionLocation.latitude = dtlPlace.coordinates.getLat();
        dtlTransactionLocation.longitude = dtlPlace.coordinates.getLng();
        return dtlTransactionLocation;
    }
}
