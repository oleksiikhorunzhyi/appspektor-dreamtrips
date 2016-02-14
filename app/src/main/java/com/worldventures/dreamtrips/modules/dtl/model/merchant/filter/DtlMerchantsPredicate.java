package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
public abstract class DtlMerchantsPredicate implements Predicate<DtlMerchant> {

    public abstract DtlFilterData getFilterData();

    public abstract LatLng getCurrentLatLng();

    public abstract DtlMerchantType getMerchantType();

    @Override
    @Value.Derived
    public boolean apply(DtlMerchant dtlMerchant) {
        return checkType(dtlMerchant)
                && applyFilter(dtlMerchant)
                && checkQuery(dtlMerchant);
    }

    /**
     * Filter-out merchants of different type
     * @param dtlMerchant merchant to filter
     * @return true if merchant of correct type
     */
    @Value.Derived
    public boolean checkType(DtlMerchant dtlMerchant) {
        return getMerchantType() == null || dtlMerchant.getMerchantType() == getMerchantType();
    }

    /**
     * Filter-out merchants that don't match search query
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes search
     */
    @Value.Derived
    public boolean checkQuery(DtlMerchant dtlMerchant) {
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

    /**
     * Apply filter on merchant
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    @Value.Derived
    private boolean applyFilter(DtlMerchant dtlMerchant) {
        return checkPrice(dtlMerchant)
                && checkDistance(dtlMerchant)
                && checkAmenities(dtlMerchant);
    }

    /**
     * Filtering criteria for merchant that checks price criteria
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    @Value.Derived
    private boolean checkPrice(DtlMerchant dtlMerchant) {
        return dtlMerchant.getBudget() >= getFilterData().getMinPrice() &&
                dtlMerchant.getBudget() <= getFilterData().getMaxPrice();
    }

    /**
     * Filtering criteria for merchant that checks distance criteria
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    @Value.Derived
    private boolean checkDistance(DtlMerchant dtlMerchant) {
        return getFilterData().getMaxDistance() == DtlFilterParameters.MAX_DISTANCE
                || getCurrentLatLng() == null
                || dtlMerchant.getDistance() < getFilterData().getMaxDistance();
    }

    /**
     * Filtering criteria for merchant that checks amenities criteria
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    @Value.Derived
    private boolean checkAmenities(DtlMerchant dtlMerchant) {
        return dtlMerchant.getAmenities() == null || dtlMerchant.getAmenities().isEmpty() ||
                !Collections.disjoint(getFilterData().getSelectedAmenities(), dtlMerchant.getAmenities());
    }
}
