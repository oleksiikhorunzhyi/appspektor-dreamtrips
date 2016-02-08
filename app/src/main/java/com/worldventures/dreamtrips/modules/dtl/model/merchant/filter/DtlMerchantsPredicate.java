package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.Collections;
import java.util.List;

public class DtlMerchantsPredicate implements Predicate<DtlMerchant> {

    private DtlFilterData dtlFilterData;
    private LatLng currentLatLng;
    //
    private String query;
    //
    private DtlMerchantType merchantType;

    private DtlMerchantsPredicate(DtlFilterData dtlFilterData, LatLng currentLatLng, String query,
                                  DtlMerchantType merchantType) {
        this.dtlFilterData = dtlFilterData;
        this.currentLatLng = currentLatLng;
        this.query = query;
        this.merchantType = merchantType;
    }

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return checkType(dtlMerchant)
                && applyFilter(dtlMerchant)
                && checkQuery(dtlMerchant);
    }

    public boolean checkType(DtlMerchant dtlMerchant) {
        return merchantType == null ||
                dtlMerchant.getMerchantType() == merchantType;
    }

    public boolean checkQuery(DtlMerchant dtlMerchant) {
        if (query == null) return false;
        //
        List<DtlMerchantAttribute> categories = dtlMerchant.getCategories();
        return dtlMerchant.getDisplayName().toLowerCase().contains(query.toLowerCase()) || (categories != null &&
                Queryable.from(categories).firstOrDefault(element ->
                        element.getName().toLowerCase().contains(query.toLowerCase())) != null);
    }

    /**
     * Apply filter on merchant
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    public boolean applyFilter(DtlMerchant dtlMerchant) {
        return checkPrice(dtlMerchant)
                && checkDistance(dtlMerchant)
                && checkAmenities(dtlMerchant);
    }

    /**
     * Filtering criteria for merchant that checks price criteria
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    public boolean checkPrice(DtlMerchant dtlMerchant) {
        return dtlMerchant.getBudget() >= dtlFilterData.getMinPrice() &&
                dtlMerchant.getBudget() <= dtlFilterData.getMaxPrice();
    }

    /**
     * Filtering criteria for merchant that checks distance criteria
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    public boolean checkDistance(DtlMerchant dtlMerchant) {
        return dtlFilterData.getMaxDistance() == DtlFilterParameters.MAX_DISTANCE
                || currentLatLng == null
                || dtlMerchant.getDistance() < dtlFilterData.getMaxDistance();
    }

    /**
     * Filtering criteria for merchant that checks amenities criteria
     * @param dtlMerchant merchant to filter
     * @return true if merchant passes filter
     */
    public boolean checkAmenities(DtlMerchant dtlMerchant) {
        return dtlMerchant.getAmenities() == null || dtlMerchant.getAmenities().isEmpty() ||
                !Collections.disjoint(dtlFilterData.getSelectedAmenities(), dtlMerchant.getAmenities());
    }

    public static class Builder {
        private DtlFilterData dtlFilterData;
        private LatLng currentLatLng;
        private String query;
        private DtlMerchantType merchantType;

        public static Builder create() {
            return new Builder();
        }

        public Builder withDtlFilterData(DtlFilterData dtlFilterData) {
            this.dtlFilterData = dtlFilterData;
            return this;
        }

        public Builder withLatLng(LatLng currentLatLng) {
            this.currentLatLng = currentLatLng;
            return this;
        }

        public Builder withQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder withMerchantType(DtlMerchantType merchantType) {
            this.merchantType = merchantType;
            return this;
        }

        public DtlMerchantsPredicate build() {
            return new DtlMerchantsPredicate(dtlFilterData, currentLatLng, query,
                    merchantType);
        }
    }
}
