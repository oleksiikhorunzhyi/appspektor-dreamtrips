package com.worldventures.dreamtrips.modules.dtl.model;

public class DtlTransactionLocation {
    private String city;
    private String state;
    private String country;
    private double latitude;
    private double longitude;

    public static DtlTransactionLocation fromDtlPlace(DTlMerchant DTlMerchant, double latitude, double longitude) {
        DtlTransactionLocation  dtlTransactionLocation = new DtlTransactionLocation();
        dtlTransactionLocation.city = DTlMerchant.getCity();
        dtlTransactionLocation.state = DTlMerchant.getState();
        dtlTransactionLocation.country = DTlMerchant.getCountry();
        dtlTransactionLocation.latitude = latitude;
        dtlTransactionLocation.longitude = longitude;
        return dtlTransactionLocation;
    }
}
