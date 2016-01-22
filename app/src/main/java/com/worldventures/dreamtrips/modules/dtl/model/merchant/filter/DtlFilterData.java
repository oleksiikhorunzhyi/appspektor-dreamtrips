package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DtlFilterData implements Parcelable {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final int MAX_DISTANCE = 50;
    // TODO : current MAX_DISTANCE assumes miles - wrong

    private int minPrice;
    private int maxPrice;
    //
    private int maxDistance;
    //
    private DistanceType distanceType;
    //
    private List<DtlMerchantAttribute> amenities = new ArrayList<>();
    private List<DtlMerchantAttribute> selectedAmenities = new ArrayList<>();

    private DtlFilterData() {
    }

    public static DtlFilterData createDefault() {
        DtlFilterData dtlFilterData = new DtlFilterData();
        dtlFilterData.reset();
        return dtlFilterData;
    }

    /**
     * Create new filter data model based on current and given as parameter.
     * To be used to update presenter and delegate filter state from UI
     * @param filterData filter data constructed from UI
     * @return mutated instance
     * <br /><br />
     * TODO : think about using proper immutable mechanism here
     */
    public DtlFilterData mutateFrom(DtlFilterData filterData) {
        DtlFilterData result = new DtlFilterData();
        result.setAmenities(this.amenities);
        result.setSelectedAmenities(filterData.getSelectedAmenities());
        result.setDistanceType(filterData.getDistanceType());
        result.setPrice(filterData.getMinPrice(), filterData.getMaxPrice());
        result.setMaxDistance(filterData.getMaxDistance());
        return result;
    }

    public void reset() {
        selectedAmenities.clear();
        selectedAmenities.addAll(amenities);
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
        // monkey-patch for unusual crashes with values out of bounds for rangebar
        this.minPrice = minPrice < MIN_PRICE ? MIN_PRICE : minPrice;
        this.maxPrice = maxPrice > MAX_PRICE ? MAX_PRICE : maxPrice;
    }

    public void setAmenities(List<DtlMerchantAttribute> amenities) {
        this.amenities.clear();
        this.amenities.addAll(amenities);
        Collections.sort(this.amenities);
    }

    public List<DtlMerchantAttribute> getAmenities() {
        return this.amenities;
    }

    public boolean hasAmenities() {
        return !amenities.isEmpty();
    }

    public List<DtlMerchantAttribute> getSelectedAmenities() {
        return selectedAmenities;
    }

    public void setSelectedAmenities(List<DtlMerchantAttribute> selectedAmenities) {
        this.selectedAmenities.clear();
        this.selectedAmenities.addAll(selectedAmenities);
    }

    public void selectAllAmenities() {
        this.selectedAmenities.clear();
        this.selectedAmenities.addAll(amenities);
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        // monkey-patch for unusual crashes with values out of bounds for rangebar
        this.maxDistance = maxDistance > MAX_DISTANCE ? MAX_DISTANCE : maxDistance;
    }


    public void setDistanceType(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    public DistanceType getDistanceType() {
        return distanceType;
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
        amenities = in.createTypedArrayList(DtlMerchantAttribute.CREATOR);
        selectedAmenities = in.createTypedArrayList(DtlMerchantAttribute.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(minPrice);
        dest.writeInt(maxPrice);
        dest.writeInt(maxDistance);
        dest.writeSerializable(distanceType);
        dest.writeTypedList(amenities);
        dest.writeTypedList(selectedAmenities);
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
}
