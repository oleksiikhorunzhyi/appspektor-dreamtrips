package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Value.Immutable
@Value.Style(privateNoargConstructor = true)
public abstract class DtlFilterParameters {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final int MAX_DISTANCE = 50;
    // TODO : current MAX_DISTANCE assumes miles - wrong

    public abstract int getMinPrice();
    public abstract int getMaxPrice();
    //
    public abstract int getMaxDistance();
    //
    public abstract List<DtlMerchantAttribute> getSelectedAmenities();

}
