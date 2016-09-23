package com.worldventures.dreamtrips.modules.map.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

public class DtlClusterItem implements ClusterItem {

   private final LatLng latLng;
   private final ThinMerchant merchant;

   public DtlClusterItem(ThinMerchant merchant) {
      this.merchant = merchant;
      this.latLng = new LatLng(merchant.coordinates().lat(), merchant.coordinates().lng());
   }

   @Override
   public LatLng getPosition() {
      return latLng;
   }

   public ThinMerchant getMerchant() {
      return merchant;
   }
}
