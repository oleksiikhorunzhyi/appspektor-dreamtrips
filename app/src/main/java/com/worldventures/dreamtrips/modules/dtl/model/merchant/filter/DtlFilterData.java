package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(privateNoargConstructor = true, defaultAsDefault = true)
@DefaultSerializer(CompatibleFieldSerializer.class)
public abstract class DtlFilterData {

    @Value.Default
    public String getSearchQuery() {
        return "";
    }

    @Value.Default
    public int getMinPrice() {
        return DtlFilterParameters.MIN_PRICE;
    }

    @Value.Default
    public int getMaxPrice() {
        return DtlFilterParameters.MAX_PRICE;
    }

    @Value.Default
    public int getMaxDistance() {
        return DtlFilterParameters.MAX_DISTANCE;
    }
    //
    @Nullable
    public abstract DistanceType getDistanceType();
    //
    public abstract List<DtlMerchantAttribute> getAmenities();
    public abstract List<DtlMerchantAttribute> getSelectedAmenities();

    public static DtlFilterData merge(DtlFilterParameters filterParameters, DtlFilterData filterData) {
        return ImmutableDtlFilterData.copyOf(filterData)
                .withMinPrice(filterParameters.getMinPrice())
                .withMaxPrice(filterParameters.getMaxPrice())
                .withMaxDistance(filterParameters.getMaxDistance())
                .withSelectedAmenities(filterParameters.getSelectedAmenities());
    }

    public void reset() {
//        from(DtlFilterParameters.createDefault());
//        selectAllAmenities();
    }

    public boolean hasAmenities() {
        return !getAmenities().isEmpty();
    }
}
