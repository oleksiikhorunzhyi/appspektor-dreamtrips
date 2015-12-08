package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
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
        List<DtlMerchantAttribute> categories = dtlMerchant.getCategories();

        return dtlMerchant.getDisplayName().toLowerCase().contains(query.toLowerCase()) || (categories != null &&
                Queryable.from(categories).firstOrDefault(element ->
                        element.getName().toLowerCase().contains(query.toLowerCase())) != null);
    }

    public boolean applyFilter(DtlMerchant dtlMerchant) {
        return checkPrice(dtlMerchant)
                && checkDistance(dtlMerchant)
                && checkAmenities(dtlMerchant);
    }

    public boolean checkPrice(DtlMerchant dtlMerchant) {
        return dtlMerchant.getBudget() >= dtlFilterData.getMinPrice() &&
                dtlMerchant.getBudget() <= dtlFilterData.getMaxPrice();
    }

    public boolean checkDistance(DtlMerchant dtlMerchant) {
        return dtlFilterData.getMaxDistance() == DtlFilterData.MAX_DISTANCE
                || currentLatLng == null
                || LocationHelper.checkLocation(dtlFilterData.getMaxDistance(),
                currentLatLng, dtlMerchant.getCoordinates().asLatLng(), dtlFilterData.getDistanceType());
    }

    public boolean checkAmenities(DtlMerchant dtlMerchant) {
        List<DtlPlacesFilterAttribute> selectedAmenities = dtlFilterData.getSelectedAmenities();
        return selectedAmenities == null || dtlMerchant.getAmenities() == null ||
                !Collections.disjoint(selectedAmenities, Queryable.from(dtlMerchant.getAmenities()).map(element ->
                                new DtlPlacesFilterAttribute(element.getName())
                ).toList());
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
            return new DtlMerchantsPredicate(dtlFilterData, currentLatLng, query, merchantType);
        }
    }

}
