package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Value.Immutable
// TODO : current MAX_DISTANCE assumes miles - wrong
public abstract class DtlFilterParameters {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final int MAX_DISTANCE = 50;

    /**
     * Gets min price.
     * @return the min price
     */
    @Value.Default
    public int getMinPrice() {
        return MIN_PRICE;
    }

    /**
     * Gets max price.
     * @return the max price
     */
    @Value.Default
    public int getMaxPrice() {
        return MAX_PRICE;
    }

    /**
     * Gets max distance.
     * @return the max distance
     */
    @Value.Default
    public int getMaxDistance() {
        return MAX_DISTANCE;
    }

    /**
     * Gets selected amenities.
     * @return the selected amenities
     */
    @Value.Default
    public List<DtlMerchantAttribute> getSelectedAmenities() {
        return Collections.emptyList();
    }

}
