package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import org.immutables.value.Value;

import java.util.List;


@Value.Immutable
public abstract class DtlFilterData {

    /**
     * Gets search query.
     * @return the search query
     */
    @Value.Default
    public String getSearchQuery() {
        return "";
    }

    /**
     * Gets min price.
     * @return the min price
     */
    @Value.Default
    public int getMinPrice() {
        return DtlFilterParameters.MIN_PRICE;
    }

    /**
     * Gets max price.
     * @return the max price
     */
    @Value.Default
    public int getMaxPrice() {
        return DtlFilterParameters.MAX_PRICE;
    }

    /**
     * Gets max distance.
     * @return the max distance
     */
    @Value.Default
    public int getMaxDistance() {
        return DtlFilterParameters.MAX_DISTANCE;
    }

    /**
     * Gets distance type.
     * @return the distance type
     */
    @Nullable
    public abstract DistanceType getDistanceType();

    /**
     * Gets amenities.
     * @return the amenities
     */
    public abstract List<DtlMerchantAttribute> getAmenities();

    /**
     * Gets selected amenities.
     * @return the selected amenities
     */
    public abstract List<DtlMerchantAttribute> getSelectedAmenities();

    /**
     * Merge dtl DtlFilterParameters and current DtlFilterData.
     * @param filterParameters the filter parameters
     * @param filterData       the filter data
     * @return merged filter data
     */
    public static DtlFilterData merge(DtlFilterParameters filterParameters, DtlFilterData filterData) {
        return ImmutableDtlFilterData.copyOf(filterData)
                .withMinPrice(filterParameters.getMinPrice())
                .withMaxPrice(filterParameters.getMaxPrice())
                .withMaxDistance(filterParameters.getMaxDistance())
                .withSelectedAmenities(filterParameters.getSelectedAmenities());
    }

    /**
     * @return true if filter data has amenities
     */
    @Value.Derived
    public boolean hasAmenities() {
        return !getAmenities().isEmpty();
    }
}
