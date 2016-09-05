package com.worldventures.dreamtrips.modules.map.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantType;

public class DtlClusterItem implements ClusterItem {

   private final LatLng latLng;
   private final String id;
   private MerchantType merchantType;

   public DtlClusterItem(String id, LatLng latLng, MerchantType merchantType) {
      this.id = id;
      this.latLng = latLng;
      this.merchantType = merchantType;
   }

   @Override
   public LatLng getPosition() {
      return latLng;
   }

   public String getId() {
      return id;
   }

   public MerchantType getMerchantType() {
      return merchantType;
   }
}
