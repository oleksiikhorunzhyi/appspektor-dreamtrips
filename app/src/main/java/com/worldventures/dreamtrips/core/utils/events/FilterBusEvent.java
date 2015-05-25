package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;

import java.util.ArrayList;
import java.util.List;


public class FilterBusEvent {

    private int minNights;
    private int maxNights;
    private double minPrice;
    private double maxPrice;
    private boolean reset;
    private boolean showSoldOut;
    private DateFilterItem dateFilterItem;
    private List<Integer> acceptedRegions = new ArrayList<>();
    private List<ActivityModel> acceptedActivities = new ArrayList<>();
    private boolean showFavorites;
    private boolean showRecentlyAdded;

    public int getMinNights() {
        return minNights;
    }

    public void setMinNights(int minNights) {
        this.minNights = minNights;
    }

    public int getMaxNights() {
        return maxNights;
    }

    public void setMaxNights(int maxNights) {
        this.maxNights = maxNights;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<Integer> getAcceptedRegions() {
        return acceptedRegions;
    }

    public void setAcceptedRegions(List<Integer> acceptedRegions) {
        this.acceptedRegions = acceptedRegions;
    }

    public List<ActivityModel> getAcceptedActivities() {
        return acceptedActivities;
    }

    public void setAcceptedActivities(List<ActivityModel> acceptedActivities) {
        this.acceptedActivities = acceptedActivities;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public boolean isShowSoldOut() {
        return showSoldOut;
    }

    public void setShowSoldOut(boolean showSoldOut) {
        this.showSoldOut = showSoldOut;
    }

    public DateFilterItem getDateFilterItem() {
        return dateFilterItem;
    }

    public void setDateFilterItem(DateFilterItem dateFilterItem) {
        this.dateFilterItem = dateFilterItem;
    }

    public void setShowFavorites(boolean showFavorites) {
        this.showFavorites = showFavorites;
    }

    public boolean isShowFavorites() {
        return showFavorites;
    }

    public void setShowRecentlyAdded(boolean showRecentlyAdded) {
        this.showRecentlyAdded = showRecentlyAdded;
    }

    public boolean isShowRecentlyAdded() {
        return showRecentlyAdded;
    }
}
