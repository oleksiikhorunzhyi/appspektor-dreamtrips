package com.worldventures.dreamtrips.modules.map.model;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

public enum ClusterType {

   OFFERS(R.drawable.cluster_offer_icon), DININGS(R.drawable.cluster_dining_icon), COMBINE(R.drawable.cluster_combine_icon), UNKNOWN(0);

   private int resource;

   ClusterType(int resource) {
      this.resource = resource;
   }

   public int asResource() {
      return resource;
   }

   public static ClusterType from(DtlClusterItem clusterItem) {
      return clusterItem.getDtlMerchantType() == DtlMerchantType.OFFER ? OFFERS : DININGS;
   }
}
