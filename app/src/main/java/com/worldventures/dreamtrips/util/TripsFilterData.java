package com.worldventures.dreamtrips.util;

import android.support.annotation.Nullable;
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

   public static final int MIN_NIGHTS = 0;
   public static final int MAX_NIGHTS = 9;
   public static final int MIN_PRICE = 100;
   public static final int MAX_PRICE = 500;

   private int minNights;
   private int maxNights;
   private double minPrice;
   private double maxPrice;
   private boolean showSoldOut;
   private boolean showFavorites;
   private boolean showRecentlyAdded;
   private Date startDate;
   private Date endDate;
   //
   private ArrayList<RegionModel> allRegions = new ArrayList<>();
   private ArrayList<ActivityModel> allParentActivities = new ArrayList<>();

   public Integer getMinNights() {
      return minNights <= MIN_NIGHTS ? null : minNights;
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

   @Nullable
   public List<Integer> getAcceptedRegions() {
      if (Queryable.from(allRegions).firstOrDefault(region -> !region.isChecked()) == null) {
         return null;
      }
      return Queryable.from(allRegions)
            .filter(RegionModel::isChecked)
            .map(BaseEntity::getId)
            .toList();
   }

   @Nullable
   public List<Integer> getAcceptedActivities() {
      if (Queryable.from(allParentActivities).firstOrDefault(activity -> !activity.isChecked()) == null) {
         return null;
      }
      return Queryable.from(allParentActivities)
            .filter(ActivityModel::isChecked)
            .map(BaseEntity::getId)
            .toList();
   }

   public void setAllRegions(ArrayList<RegionModel> allRegions) {
      this.allRegions = allRegions;
   }

   public void setAllParentActivities(ArrayList<ActivityModel> allParentActivities) {
      this.allParentActivities = allParentActivities;
   }

   public ArrayList<RegionModel> getAllRegions() {
      return allRegions;
   }

   public ArrayList<ActivityModel> getAllParentActivities() {
      return allParentActivities;
   }

   public boolean isShowSoldOut() {
      return showSoldOut;
   }

   public void setShowSoldOut(boolean showSoldOut) {
      this.showSoldOut = showSoldOut;
   }

   public String getStartDateFormatted() {
      return DateTimeUtils.convertDateToString(startDate, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
   }

   public void setStartDate(Date startDate) {
      this.startDate = startDate;
   }

   public String getEndDateFormatted() {
      return DateTimeUtils.convertDateToString(endDate, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
   }

   public Date getStartDate() {
      return startDate;
   }

   public Date getEndDate() {
      return endDate;
   }

   public void setEndDate(Date endDate) {
      this.endDate = endDate;
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

   public static TripsFilterData createDefault(SnappyRepository db) {
      TripsFilterData tripsFilterData = new TripsFilterData();
      tripsFilterData.maxPrice = MAX_PRICE;
      tripsFilterData.minPrice = MIN_PRICE;
      tripsFilterData.maxNights = MAX_NIGHTS;
      tripsFilterData.minNights = MIN_NIGHTS;
      tripsFilterData.showSoldOut = false;

      Calendar calendar = Calendar.getInstance();
      tripsFilterData.startDate = calendar.getTime();
      calendar.add(Calendar.MONTH, 12);
      tripsFilterData.endDate = calendar.getTime();

      tripsFilterData.allRegions = new ArrayList<>();
      tripsFilterData.allRegions.addAll(getRegions(db));

      tripsFilterData.allParentActivities = new ArrayList<>();
      tripsFilterData.allParentActivities.addAll(getThemes(db));

      tripsFilterData.showFavorites = false;
      tripsFilterData.showRecentlyAdded = false;

      return tripsFilterData;
   }

   private static List<RegionModel> getRegions(SnappyRepository db) {
      return db.readList(SnappyRepository.REGIONS, RegionModel.class);
   }

   private static List<ActivityModel> getParentActivities(List<ActivityModel> activities) {
      return Queryable.from(activities).filter(ActivityModel::isParent).toList();
   }

   private static ArrayList<ActivityModel> getThemes(SnappyRepository db) {
      List<ActivityModel> activities = db.readList(SnappyRepository.ACTIVITIES, ActivityModel.class);
      List<ActivityModel> parentActivities = getParentActivities(activities);

      return new ArrayList<>(parentActivities);
   }
}
