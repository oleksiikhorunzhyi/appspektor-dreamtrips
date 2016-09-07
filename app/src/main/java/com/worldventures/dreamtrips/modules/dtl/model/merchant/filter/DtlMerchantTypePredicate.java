package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantType;

/**
 * Opt out merchants of type other than specified
 */
public class DtlMerchantTypePredicate implements Predicate<DtlMerchant> {

   private final MerchantType merchantType;

   public DtlMerchantTypePredicate(MerchantType merchantType) {
      this.merchantType = merchantType;
   }

   public MerchantType getMerchantType() {
      return merchantType;
   }

   @Override
   public boolean apply(DtlMerchant dtlMerchant) {
      return merchantType == null || dtlMerchant.getMerchantType() == merchantType;
   }
}
