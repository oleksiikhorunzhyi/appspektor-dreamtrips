package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.os.Parcel;
import android.os.Parcelable;

import com.innahema.collections.query.queriables.Queryable;

import java.util.ArrayList;
import java.util.List;

public class DtlFilterData implements Parcelable {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final int MAX_DISTANCE = 50;

    private int minPrice;
    private int maxPrice;

    private int maxDistance;

    private DistanceType distanceType;

    private List<DtlMerchantsFilterAttribute> amenities;

    public DtlFilterData() {
        reset();
    }

    public void reset() {
        if (amenities == null) amenities = new ArrayList<>();
        Queryable.from(amenities).forEachR(amenity -> amenity.setChecked(true));
        minPrice = MIN_PRICE;
        maxPrice = MAX_PRICE;
        maxDistance = MAX_DISTANCE;
        distanceType = DistanceType.MILES;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setPrice(int minPrice, int maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public List<DtlMerchantsFilterAttribute> getAmenities() {
        return amenities;
    }

    public List<DtlMerchantsFilterAttribute> getSelectedAmenities() {
        return amenities != null && !amenities.isEmpty()
                ? Queryable.from(amenities).filter(DtlMerchantsFilterAttribute::isChecked).toList()
                : null;
    }

    public void toggleAmenitiesSelection(boolean selected) {
        if (amenities != null) {
            for (DtlMerchantsFilterAttribute attribute : amenities) {
                attribute.setChecked(selected);
            }
        }
    }

    public void setAmenities(List<DtlMerchantsFilterAttribute> amenities) {
        this.amenities = amenities;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setDistanceType(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public DistanceType getDistanceType() {
        return distanceType;
    }

    public void toggleDistance() {
        if (distanceType == DistanceType.KMS) distanceType = DistanceType.MILES;
        else distanceType = DistanceType.KMS;
    }

    public enum DistanceType {
        MILES("ml", true), KMS("km", false);

        boolean selected;
        String analyticsTypeName;

        DistanceType(String analyticsTypeName, boolean selected) {
            this.analyticsTypeName = analyticsTypeName;
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public String getTypeNameForAnalytics() {
            return analyticsTypeName;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlFilterData(Parcel in) {
        minPrice = in.readInt();
        maxPrice = in.readInt();
        maxDistance = in.readInt();
        distanceType = (DistanceType) in.readSerializable();
        amenities = in.createTypedArrayList(DtlMerchantsFilterAttribute.CREATOR);
    }

    public static final Creator<DtlFilterData> CREATOR = new Creator<DtlFilterData>() {
        @Override
        public DtlFilterData createFromParcel(Parcel in) {
            return new DtlFilterData(in);
        }

        @Override
        public DtlFilterData[] newArray(int size) {
            return new DtlFilterData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(minPrice);
        dest.writeInt(maxPrice);
        dest.writeInt(maxDistance);
        dest.writeSerializable(distanceType);
        dest.writeTypedList(amenities);
    }

}
