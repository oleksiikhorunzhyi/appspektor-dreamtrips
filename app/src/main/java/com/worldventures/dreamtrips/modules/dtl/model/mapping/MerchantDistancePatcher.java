package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

import rx.functions.Func1;

public class MerchantDistancePatcher implements Func1<Merchant, Merchant> {

   private final DtlLocation dtlLocation;

   public static MerchantDistancePatcher create(DtlLocation dtlLocation) {
      return new MerchantDistancePatcher(dtlLocation);
   }

   private MerchantDistancePatcher(DtlLocation dtlLocation) {
      this.dtlLocation = dtlLocation;
   }

   @Override
   public Merchant call(Merchant merchant) {
      return ImmutableMerchant.copyOf(merchant)
            .withDistance(calculateDistance(dtlLocation, merchant));
   }

   private static double calculateDistance(DtlLocation dtlLocation, Merchant merchant) {
      return DtlLocationHelper.calculateDistance(dtlLocation.getCoordinates().asLatLng(),
            new LatLng(merchant.coordinates().lat(), merchant.coordinates().lng())) / 1000;
   }
}
