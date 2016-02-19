package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import org.immutables.value.Value;

/**
 * Opt out merchants of type other than specified
 */
@Value.Immutable
public abstract class DtlMerchantTypePredicate implements Predicate<DtlMerchant> {

    @Value.Parameter
    public abstract DtlMerchantType getMerchantType();

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return getMerchantType() == null || dtlMerchant.getMerchantType() == getMerchantType();
    }
}
