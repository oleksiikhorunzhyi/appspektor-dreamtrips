package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import java.util.ArrayList;
import java.util.List;

public class DtlFilterParameters {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final int MAX_DISTANCE = 50;
    // TODO : current MAX_DISTANCE assumes miles - wrong

    private int minPrice;
    private int maxPrice;
    //
    private int maxDistance;
    //
    private List<DtlMerchantAttribute> selectedAmenities = new ArrayList<>();

    private DtlFilterParameters() {
    }

    public static DtlFilterParameters createDefault() {
        DtlFilterParameters filter = new DtlFilterParameters();
        filter.minPrice = MIN_PRICE;
        filter.maxPrice = MAX_PRICE;
        filter.maxDistance = MAX_DISTANCE;
        return filter;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public List<DtlMerchantAttribute> getSelectedAmenities() {
        return selectedAmenities;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    public static class Builder {

        private DtlFilterParameters filter;

        private Builder() {
        }

        public static DtlFilterParameters.Builder create() {
            DtlFilterParameters.Builder builder = new DtlFilterParameters.Builder();
            builder.filter = createDefault();
            return builder;
        }

        public DtlFilterParameters.Builder selectedAmenities(List<DtlMerchantAttribute> amenities) {
            filter.selectedAmenities = amenities;
            return this;
        }

        public DtlFilterParameters.Builder price(int minPrice, int maxPrice) {
            filter.minPrice = minPrice;
            filter.maxPrice = maxPrice;
            return this;
        }

        public DtlFilterParameters.Builder maxDistance(int maxDistance) {
            filter.maxDistance = maxDistance;
            return this;
        }

        public DtlFilterParameters build() {
            return filter;
        }
    }
}
