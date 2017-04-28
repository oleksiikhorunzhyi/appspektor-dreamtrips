package com.worldventures.dreamtrips.modules.dtl.model.transaction;

import com.google.gson.annotations.SerializedName;

public class DtlTransactionLocation {

   @SerializedName("ll")
   private String ll;

   public static DtlTransactionLocation fromLatLng(double latitude, double longitude) {
      DtlTransactionLocation dtlTransactionLocation = new DtlTransactionLocation();
      dtlTransactionLocation.ll = String.valueOf(latitude) + "," + String.valueOf(longitude);
      return dtlTransactionLocation;
   }
}
