package com.worldventures.dreamtrips.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TripsFilterData implements Serializable {


    private static final int MAX_NIGHTS = 9;
    private static final int MAX_PRICE = 500;

    private int minNights;
    private int maxNights;
    private double minPrice;
    private double maxPrice;
    private boolean showSoldOut;
    private ArrayList<RegionModel> acceptedRegions = new ArrayList<>();
    private ArrayList<ActivityModel> acceptedActivities = new ArrayList<>();
    private boolean showFavorites;
    private boolean showRecentlyAdded;
    private Date startDate;
    private Date endDate;

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

    public String getAcceptedRegions() {
        return TextUtils.join(",", acceptedRegions);
    }

    public void setAcceptedRegions(ArrayList<RegionModel> acceptedRegions) {
        this.acceptedRegions = acceptedRegions;
    }

    public String getAcceptedActivities() {
        return TextUtils.join(",", acceptedActivities);
    }

    public void setAcceptedActivities(ArrayList<ActivityModel> acceptedActivities) {
        this.acceptedActivities = acceptedActivities;
    }

    public int isShowSoldOut() {
        return showSoldOut ? 1 : 0;
    }

    public void setShowSoldOut(boolean showSoldOut) {
        this.showSoldOut = showSoldOut;
    }

    public String getStartDate() {
        return DateTimeUtils.convertDateToUTCString(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return DateTimeUtils.convertDateToUTCString(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setShowFavorites(boolean showFavorites) {
        this.showFavorites = showFavorites;
    }

    public int isShowFavorites() {
        return showFavorites ? 1 : 0;
    }

    public void setShowRecentlyAdded(boolean showRecentlyAdded) {
        this.showRecentlyAdded = showRecentlyAdded;
    }

    public int isShowRecentlyAdded() {
        return showRecentlyAdded ? 1 : 0;
    }

    public static TripsFilterData createDefault(SnappyRepository db) {
        TripsFilterData tripsFilterData = new TripsFilterData();
        tripsFilterData.maxPrice = MAX_PRICE;
        tripsFilterData.minPrice = 0;
        tripsFilterData.maxNights = MAX_NIGHTS;
        tripsFilterData.minNights = 0;
        tripsFilterData.showSoldOut = false;

        Calendar calendar = Calendar.getInstance();
        tripsFilterData.startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 12);
        tripsFilterData.endDate = calendar.getTime();

        tripsFilterData.acceptedRegions = new ArrayList<>();
        tripsFilterData.acceptedRegions.addAll(getRegions(db));

        tripsFilterData.acceptedActivities = new ArrayList<>();
        tripsFilterData.acceptedActivities.addAll(getThemes(db));

        tripsFilterData.showFavorites = false;
        tripsFilterData.showRecentlyAdded = false;

        return tripsFilterData;
    }

    protected static List<RegionModel> getRegions(SnappyRepository db) {
        return db.readList(SnappyRepository.REGIONS, RegionModel.class);
    }

    private static List<ActivityModel> getParentActivities(List<ActivityModel> activities) {
        return Queryable.from(activities).filter(input -> input.getParentId() == 0).toList();
    }

    private static ArrayList<ActivityModel> getThemes(SnappyRepository db) {
        List<ActivityModel> activities = db.readList(SnappyRepository.ACTIVITIES, ActivityModel.class);
        List<ActivityModel> parentActivities = getParentActivities(activities);

        ArrayList<ActivityModel> themesList = new ArrayList<>();
        for (ActivityModel activity : parentActivities) {
            themesList.addAll(Queryable.from(activities)
                    .filter((input) -> input.getParentId() == activity.getId())
                    .toList());
            themesList.add(activity);

        }
        return themesList;
    }


}
