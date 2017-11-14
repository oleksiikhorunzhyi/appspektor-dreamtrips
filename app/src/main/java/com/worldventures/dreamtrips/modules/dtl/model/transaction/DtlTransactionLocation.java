package com.worldventures.dreamtrips.modules.dtl.model.transaction;

import com.google.gson.annotations.SerializedName;

public class DtlTransactionLocation {

   @SerializedName("ll")
   private String ll; //NOPMD TODO: Do we need it here?

   public static DtlTransactionLocation fromLatLng(double latitude, double longitude) {
      DtlTransactionLocation dtlTransactionLocation = new DtlTransactionLocation();
      dtlTransactionLocation.ll = latitude + "," + longitude;
      return dtlTransactionLocation;
   }
}
