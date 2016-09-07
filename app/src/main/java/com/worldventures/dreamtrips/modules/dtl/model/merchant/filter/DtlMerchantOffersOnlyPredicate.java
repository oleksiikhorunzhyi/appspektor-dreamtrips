package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantType;

public class DtlMerchantOffersOnlyPredicate implements Predicate<DtlMerchant> {

   private final boolean offersOnly;

   public DtlMerchantOffersOnlyPredicate(DtlFilterData filterData) {
      offersOnly = filterData.isOffersOnly();
   }

   @Override
   public boolean apply(DtlMerchant dtlMerchant) {
      return !(offersOnly && dtlMerchant.getMerchantType() == MerchantType.DINING);
   }
}
