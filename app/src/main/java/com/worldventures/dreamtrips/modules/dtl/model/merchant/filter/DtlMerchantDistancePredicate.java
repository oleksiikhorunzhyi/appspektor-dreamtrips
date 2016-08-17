package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

/**
 * Opt out merchant that is out of range, specified in filter
 */
public class DtlMerchantDistancePredicate implements Predicate<DtlMerchant> {

   private final DtlFilterData filterData;

   public DtlMerchantDistancePredicate(DtlFilterData filterData) {
      this.filterData = filterData;
   }

   @Override
   public boolean apply(DtlMerchant dtlMerchant) {
      return filterData.getMaxDistance() == DtlFilterParameters.MAX_DISTANCE || dtlMerchant.getDistance() < filterData.getMaxDistance();
   }
}
