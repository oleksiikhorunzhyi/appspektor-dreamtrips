package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

/**
 * Opt out merchants of type other than specified
 */
public class DtlMerchantTypePredicate implements Predicate<DtlMerchant> {

    private final DtlMerchantType merchantType;

    public DtlMerchantTypePredicate(DtlMerchantType merchantType) {
        this.merchantType = merchantType;
    }

    public DtlMerchantType getMerchantType() {
        return merchantType;
    }

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return merchantType == null || dtlMerchant.getMerchantType() == merchantType;
    }
}
