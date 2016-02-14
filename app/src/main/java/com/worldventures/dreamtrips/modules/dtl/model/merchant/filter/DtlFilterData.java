package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DtlFilterData {

    private String searchQuery = "";
    //
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
        return new DtlFilterData().from(DtlFilterParameters.createDefault());
    }

    public DtlFilterData from(DtlFilterParameters filterParameters) {
        this.minPrice = filterParameters.getMinPrice();
        this.maxPrice = filterParameters.getMaxPrice();
        this.maxDistance = filterParameters.getMaxDistance();
        this.selectedAmenities = filterParameters.getSelectedAmenities();
        return this;
    }

    public void reset() {
        from(DtlFilterParameters.createDefault());
        selectAllAmenities();
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
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

    public void selectAllAmenities() {
        this.selectedAmenities.clear();
        this.selectedAmenities.addAll(amenities);
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setDistanceType(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    public DistanceType getDistanceType() {
        return distanceType;
    }
}
