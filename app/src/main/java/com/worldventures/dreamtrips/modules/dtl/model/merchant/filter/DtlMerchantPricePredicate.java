package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import org.immutables.value.Value;

/**
 * Opt out merchants that don't match search filter criteria
 */
@Value.Immutable
public abstract class DtlMerchantPricePredicate implements Predicate<DtlMerchant> {

    @Value.Parameter
    public abstract DtlFilterData getFilterData();

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return dtlMerchant.getBudget() >= getFilterData().getMinPrice() &&
                dtlMerchant.getBudget() <= getFilterData().getMaxPrice();
    }
}
