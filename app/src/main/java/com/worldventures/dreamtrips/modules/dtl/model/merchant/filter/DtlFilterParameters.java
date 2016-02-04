package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import java.util.ArrayList;
import java.util.List;

public class DtlFilterParameters implements Parcelable {

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

        public DtlFilterParameters.Builder minPrice(int minPrice) {
            filter.minPrice = minPrice;
            return this;
        }

        public DtlFilterParameters.Builder maxPrice(int maxPrice) {
            filter.maxPrice = maxPrice;
            return this;
        }

        public DtlFilterParameters.Builder price(int minPrice, int maxPrice) {
            filter.minPrice = minPrice < MIN_PRICE ? MIN_PRICE : minPrice;
            filter.maxPrice = maxPrice > MAX_PRICE ? MAX_PRICE : maxPrice;
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

    protected DtlFilterParameters(Parcel in) {
        minPrice = in.readInt();
        maxPrice = in.readInt();
        maxDistance = in.readInt();
        selectedAmenities = in.createTypedArrayList(DtlMerchantAttribute.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(minPrice);
        dest.writeInt(maxPrice);
        dest.writeInt(maxDistance);
        dest.writeTypedList(selectedAmenities);
    }

    public static final Parcelable.Creator<DtlFilterParameters> CREATOR = new Parcelable.Creator<DtlFilterParameters>() {
        @Override
        public DtlFilterParameters createFromParcel(Parcel in) {
            return new DtlFilterParameters(in);
        }

        @Override
        public DtlFilterParameters[] newArray(int size) {
            return new DtlFilterParameters[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
