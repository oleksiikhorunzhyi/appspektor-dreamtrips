package com.worldventures.dreamtrips.modules.trips.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.util.TripsFilterData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripsFilterDataAnalyticsWrapper {

   public static final String SHOW_FAVORITES_ABBREVIATION = "SFT";
   public static final String SHOW_RECENTLY_ADDED_ABBREVIATION = "RA";
   public static final String SHOW_SOLD_OUT_ABBREVIATION = "SSOT";

   public static final String ALL = "All";

   public static final String NIGHTS_FORMAT = "%d-%d";
   public static final String PRICE_FORMAT = "%.0f-%.0f";

   public static final String DEFAULT_FIELDS_DELIMITER = ",";
   public static final String BOOLEAN_FIELDS_DELIMITER = ",";

   public static final String ATTRIBUTE_TRIP_FILTERS = "tripfilters";
   public static final String ATTRIBUTE_TRIP_REGION_FILTERS = "tripregionfilters";
   public static final String ATTRIBUTE_TRIP_THEME_FILTERS = "tripthemefilters";

   private final int minNights;
   private final int maxNights;
   private final double minPrice;
   private final double maxPrice;
   private final String startDate;
   private final String endDate;
   private final boolean isShowFavorites;
   private final boolean isShowRecentlyAdded;
   private final boolean isShowSoldOut;
   private final List<RegionModel> allRegions;
   private final List<ActivityModel> allParentActivities;

   public TripsFilterDataAnalyticsWrapper(@NonNull TripsFilterData tripsFilterData) {
      this.minNights = tripsFilterData.getMinNights() == null ? TripsFilterData.MIN_NIGHTS : tripsFilterData.getMinNights();
      this.maxNights = tripsFilterData.getMaxNights() == null ? TripsFilterData.MAX_NIGHTS : tripsFilterData.getMaxNights();
      this.minPrice = tripsFilterData.getMinPrice() == null ? TripsFilterData.MIN_PRICE : tripsFilterData.getMinPrice();
      this.maxPrice = tripsFilterData.getMaxPrice() == null ? TripsFilterData.MAX_PRICE : tripsFilterData.getMaxPrice();
      //
      SimpleDateFormat sdf = new SimpleDateFormat(DateTimeUtils.TRIP_FILTER_ANALYTIC_DATE_FORMAT, LocaleHelper.getDefaultLocale());
      this.startDate = DateTimeUtils.convertDateToString(tripsFilterData.getStartDate(), sdf);
      this.endDate = DateTimeUtils.convertDateToString(tripsFilterData.getEndDate(), sdf);
      //
      this.isShowFavorites = tripsFilterData.isShowFavorites();
      this.isShowRecentlyAdded = tripsFilterData.isShowRecentlyAdded();
      this.isShowSoldOut = tripsFilterData.isShowSoldOut();
      //
      this.allRegions = tripsFilterData.getAllRegions();
      this.allParentActivities = tripsFilterData.getAllParentActivities();
   }

   public String getFilterAnalyticString() {
      List<String> filters = new ArrayList<>();
      filters.add(String.format(LocaleHelper.getDefaultLocale(), NIGHTS_FORMAT, minNights, maxNights));
      filters.add(String.format(LocaleHelper.getDefaultLocale(), PRICE_FORMAT, minPrice, maxPrice));
      filters.add(startDate);
      filters.add(endDate);
      if (!TextUtils.isEmpty(getBooleanFieldsAnalyticString())) {
         filters.add(getBooleanFieldsAnalyticString());
      }
      return TextUtils.join(DEFAULT_FIELDS_DELIMITER, filters);
   }

   public String getAcceptedRegionsAnalyticString() {
      if (allRegions == null || allRegions.isEmpty()) {
         return ALL;
      }
      if (Queryable.from(allRegions).count(region -> !region.isChecked()) == 0) {
         return ALL;
      }
      //
      return TextUtils.join(DEFAULT_FIELDS_DELIMITER, Queryable.from(allRegions)
            .filter(RegionModel::isChecked)
            .map(region -> String.valueOf(region.getId()))
            .toList());
   }

   public String getAcceptedActivitiesAnalyticString() {
      if (allParentActivities == null || allParentActivities.isEmpty()) {
         return ALL;
      }
      if (Queryable.from(allParentActivities).count(activity -> !activity.isChecked()) == 0) {
         return ALL;
      }
      //
      return TextUtils.join(DEFAULT_FIELDS_DELIMITER, Queryable.from(allParentActivities)
            .filter(ActivityModel::isChecked)
            .map(activity -> String.valueOf(activity.getId()))
            .toList());
   }

   @Nullable
   private String getBooleanFieldsAnalyticString() {
      List<String> booleanFields = new ArrayList<>();
      if (isShowFavorites) {
         booleanFields.add(SHOW_FAVORITES_ABBREVIATION);
      }
      if (isShowRecentlyAdded) {
         booleanFields.add(SHOW_RECENTLY_ADDED_ABBREVIATION);
      }
      if (isShowSoldOut) {
         booleanFields.add(SHOW_SOLD_OUT_ABBREVIATION);
      }
      return !booleanFields.isEmpty() ? TextUtils.join(BOOLEAN_FIELDS_DELIMITER, booleanFields) : null;
   }

   public Map getTrackingFilterData() {
      final Map filterData = new HashMap();
      filterData.put(ATTRIBUTE_TRIP_FILTERS, getFilterAnalyticString());
      filterData.put(ATTRIBUTE_TRIP_REGION_FILTERS, getAcceptedRegionsAnalyticString());
      filterData.put(ATTRIBUTE_TRIP_THEME_FILTERS, getAcceptedActivitiesAnalyticString());
      return filterData;
   }
}
