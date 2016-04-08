package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import java.util.List;

/**
 * Opt out merchants that don't match search query
 */
public class DtlMerchantQueryPredicate implements Predicate<DtlMerchant> {

    private final String searchQuery;

    public DtlMerchantQueryPredicate(DtlFilterData filterData) {
        if (filterData.getSearchQuery() == null) searchQuery = null;
        else searchQuery = filterData.getSearchQuery().toLowerCase();
    }

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        if (searchQuery == null) return false;
        //
        List<DtlMerchantAttribute> categories = dtlMerchant.getCategories();
        //
        return dtlMerchant.getDisplayName().toLowerCase()
                .contains(searchQuery) || (categories != null &&
                Queryable.from(categories).firstOrDefault(element ->
                        element.getName().toLowerCase().contains(searchQuery)) != null);
    }
}
