package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import org.immutables.value.Value;

import java.util.List;

/**
 * Opt out merchants that don't match search query
 * @param dtlMerchant merchant to filter
 * @return true if merchant passes search
 */
@Value.Immutable
public abstract class DtlMerchantQueryPredicate implements Predicate<DtlMerchant> {

    @Value.Parameter
    public abstract DtlFilterData getFilterData();

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        if (getFilterData().getSearchQuery() == null) return false;
        //
        String queryLowerCase = getFilterData().getSearchQuery().toLowerCase();
        //
        List<DtlMerchantAttribute> categories = dtlMerchant.getCategories();
        //
        return dtlMerchant.getDisplayName().toLowerCase()
                .contains(queryLowerCase) || (categories != null &&
                Queryable.from(categories).firstOrDefault(element ->
                        element.getName().toLowerCase().contains(queryLowerCase)) != null);
    }
}
