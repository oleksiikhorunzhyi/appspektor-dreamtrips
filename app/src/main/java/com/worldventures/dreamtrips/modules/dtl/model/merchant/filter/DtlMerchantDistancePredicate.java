package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import org.immutables.value.Value;

/**
 * Opt out merchant that is out of range, specified in filter
 */
@Value.Immutable
public abstract class DtlMerchantDistancePredicate implements Predicate<DtlMerchant> {

    @Value.Parameter
    public abstract DtlFilterData getFilterData();

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return getFilterData().getMaxDistance() == DtlFilterParameters.MAX_DISTANCE
                || dtlMerchant.getDistance() < getFilterData().getMaxDistance();
    }
}
