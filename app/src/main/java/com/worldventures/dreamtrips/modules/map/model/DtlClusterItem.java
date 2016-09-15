package com.worldventures.dreamtrips.modules.map.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

public class DtlClusterItem implements ClusterItem {

   private final LatLng latLng;
   private final String id;
   private DtlMerchantType dtlMerchantType;

   public DtlClusterItem(String id, LatLng latLng, DtlMerchantType dtlMerchantType) {
      this.id = id;
      this.latLng = latLng;
      this.dtlMerchantType = dtlMerchantType;
   }

   @Override
   public LatLng getPosition() {
      return latLng;
   }

   public String getId() {
      return id;
   }

   public DtlMerchantType getDtlMerchantType() {
      return dtlMerchantType;
   }
}
