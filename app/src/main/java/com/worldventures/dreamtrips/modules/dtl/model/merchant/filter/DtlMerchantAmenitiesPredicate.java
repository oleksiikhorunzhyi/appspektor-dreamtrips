package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.Collections;

/**
 * Opt out merchants that don't qualify for selected set of merchants
 */
public class DtlMerchantAmenitiesPredicate implements Predicate<DtlMerchant> {

    private final DtlFilterData filterData;

    public DtlMerchantAmenitiesPredicate(DtlFilterData filterData) {
        this.filterData = filterData;
    }

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return dtlMerchant.getAmenities() == null || dtlMerchant.getAmenities().isEmpty() ||
                !Collections.disjoint(filterData.getSelectedAmenities(), dtlMerchant.getAmenities());
    }
}
