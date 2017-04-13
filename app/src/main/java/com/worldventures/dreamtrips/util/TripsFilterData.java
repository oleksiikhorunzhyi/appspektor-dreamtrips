package com.worldventures.dreamtrips.util;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
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
   private List<RegionModel> allRegions = new ArrayList<>();
   private List<ActivityModel> allParentActivities = new ArrayList<>();

   public TripsFilterData() {
      maxPrice = MAX_PRICE;
      minPrice = MIN_PRICE;
      maxNights = MAX_NIGHTS;
      minNights = MIN_NIGHTS;
      showSoldOut = false;

      Calendar calendar = Calendar.getInstance();
      startDate = calendar.getTime();
      calendar.add(Calendar.MONTH, 12);
      endDate = calendar.getTime();

      showFavorites = false;
      showRecentlyAdded = false;
   }

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

   public void setAllRegions(List<RegionModel> allRegions) {
      this.allRegions = allRegions;
   }

   public void setAllParentActivities(List<ActivityModel> allParentActivities) {
      this.allParentActivities = allParentActivities;
   }

   public List<RegionModel> getAllRegions() {
      return allRegions;
   }

   public List<ActivityModel> getAllParentActivities() {
      return allParentActivities;
   }

   public boolean isShowSoldOut() {
      return showSoldOut;
   }

   public void setShowSoldOut(boolean showSoldOut) {
      this.showSoldOut = showSoldOut;
   }

   public void setStartDate(Date startDate) {
      this.startDate = startDate;
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
}
