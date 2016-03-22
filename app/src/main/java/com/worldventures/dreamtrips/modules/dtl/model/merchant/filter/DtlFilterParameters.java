package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
// TODO : current MAX_DISTANCE assumes miles - wrong
public abstract class DtlFilterParameters {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final double MAX_DISTANCE = 50;

    @Value.Default
    public int getMinPrice() {
        return MIN_PRICE;
    }

    @Value.Default
    public int getMaxPrice() {
        return MAX_PRICE;
    }

    @Value.Default
    public double getMaxDistance() {
        return MAX_DISTANCE;
    }


    @Value.Default
    public List<DtlMerchantAttribute> getSelectedAmenities() {
        return Collections.emptyList();
    }

}
