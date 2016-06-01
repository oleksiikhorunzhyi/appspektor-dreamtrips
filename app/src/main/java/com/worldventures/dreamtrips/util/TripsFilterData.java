package com.worldventures.dreamtrips.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripsFilterData implements Serializable {


    private static final int MIN_NIGHT = 0;
    private static final int MAX_NIGHTS = 9;
    private static final int MIN_PRICE = 100;
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

    public Integer getMinNights() {
        return minNights <= MIN_NIGHT ? null : minNights;
    }

    public void setMinNights(int minNights) {
        this.minNights = minNights;
    }

    public Integer getMaxNights() {
        return maxNights >= MAX_NIGHTS ? null : maxNights;
    }

    public void setMaxNights(int maxNights) {
        this.maxNights = maxNights;
    }

    public Double getMinPrice() {
        return minPrice <= MIN_PRICE ? null : minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice >= MAX_PRICE ? null : maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getAcceptedRegions() {
        return TextUtils.join(",", Queryable.from(acceptedRegions).map(BaseEntity::getId).toList());
    }

    public void setAcceptedRegions(ArrayList<RegionModel> acceptedRegions) {
        this.acceptedRegions = acceptedRegions;
    }

    public String getAcceptedActivities() {
        return TextUtils.join(",", Queryable.from(acceptedActivities).map(BaseEntity::getId).toList());
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
        return DateTimeUtils.convertDateToString(startDate, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return DateTimeUtils.convertDateToString(endDate, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
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
        tripsFilterData.minPrice = MIN_PRICE;
        tripsFilterData.maxNights = MAX_NIGHTS;
        tripsFilterData.minNights = MIN_NIGHT;
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

    private static List<RegionModel> getRegions(SnappyRepository db) {
        return db.readList(SnappyRepository.REGIONS, RegionModel.class);
    }

    private static List<ActivityModel> getParentActivities(List<ActivityModel> activities) {
        return Queryable.from(activities).filter(input -> input.getParentId() == 0).toList();
    }

    private static ArrayList<ActivityModel> getThemes(SnappyRepository db) {
        List<ActivityModel> activities = db.readList(SnappyRepository.ACTIVITIES, ActivityModel.class);
        List<ActivityModel> parentActivities = getParentActivities(activities);

        return new ArrayList<>(parentActivities);
    }

    public String getFilterAnalyticString() {
        List<String> filters = new ArrayList<>();
        filters.add(String.format("%d-%d", minNights, maxNights));
        filters.add(String.format("%f-%f", minPrice, maxPrice));
        filters.add(DateTimeUtils.convertDateToString(startDate, new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())));
        filters.add(DateTimeUtils.convertDateToString(endDate, new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())));
        filters.add(String.valueOf(showSoldOut));
        filters.add(String.valueOf(showRecentlyAdded));
        filters.add(String.valueOf(showFavorites));
        return TextUtils.join(",", filters);
    }

}
