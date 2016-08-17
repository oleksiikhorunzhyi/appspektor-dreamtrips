package com.worldventures.dreamtrips.modules.dtl.model.transaction;

public class DtlTransactionLocation {
   //
   private String ll;

   public static DtlTransactionLocation fromLatLng(double latitude, double longitude) {
      DtlTransactionLocation dtlTransactionLocation = new DtlTransactionLocation();
      dtlTransactionLocation.ll = String.valueOf(latitude) + "," + String.valueOf(longitude);
      return dtlTransactionLocation;
   }
}
