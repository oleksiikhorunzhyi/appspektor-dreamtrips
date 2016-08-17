package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A complex merchant predicate that opts-out given merchant if at least one<br />
 * supplied predicates fails.
 */
public class DtlMerchantsPredicate implements Predicate<DtlMerchant> {

   private final List<Predicate<DtlMerchant>> predicats;

   public static DtlMerchantsPredicate fromFilterData(DtlFilterData filterData) {
      return new DtlMerchantsPredicate(new DtlMerchantDistancePredicate(filterData), new DtlMerchantAmenitiesPredicate(filterData), new DtlMerchantPricePredicate(filterData), new DtlMerchantQueryPredicate(filterData), new DtlMerchantOffersOnlyPredicate(filterData));
   }

   @SafeVarargs
   public DtlMerchantsPredicate(Predicate<DtlMerchant>... predicats) {
      this.predicats = Collections.unmodifiableList(Arrays.asList(predicats));
   }

   @Override
   public boolean apply(DtlMerchant merchant) {
      for (Predicate<DtlMerchant> predicate : predicats) {
         if (!predicate.apply(merchant)) return false;
      }
      return true;
   }
}
