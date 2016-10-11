package com.worldventures.dreamtrips.modules.dtl.helper;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public abstract class FilterHelper {

   private static final double MILES_MULTIPLIER = 1.60934;
   private static final double[] MILES_VALUES = {10, 20, 30, 40, 50};
   private static final String DISTANCE_VALUE_FORMAT = "%.0f";

   public static String provideLeftDistanceValueCaption(FilterData filterData) {
      return String.format(Locale.US, DISTANCE_VALUE_FORMAT, provideLeftDistanceValue(filterData));
   }

   public static String provideRightDistanceValueCaption(FilterData filterData) {
      return String.format(Locale.US, DISTANCE_VALUE_FORMAT, provideRightDistanceValue(filterData));
   }

   public static float provideLeftDistanceValue(FilterData filterData) {
      return filterData.distanceType() == DistanceType.KMS ? 16 : 10;
   }

   public static float provideRightDistanceValue(FilterData filterData) {
      return filterData.distanceType() == DistanceType.KMS ? 80 : 50;
   }

   public static double provideDistanceByIndex(FilterData filterData) {
      double value = MILES_VALUES[filterData.distanceMaxIndex()];
      if (filterData.distanceType() == DistanceType.KMS) value = value * MILES_MULTIPLIER;
      return new BigDecimal(value).setScale(1, RoundingMode.DOWN).doubleValue();
   }

   public static float provideDistancePickerInterval(FilterData filterData) {
      return filterData.distanceType() == DistanceType.KMS ? (float) 16 : 10;
   }

   public static DistanceType provideDistanceFromSettings(SnappyRepository snappyRepository) {
      final Setting distanceTypeSetting = Queryable.from(snappyRepository.getSettings())
            .filter(setting -> setting.getName().equals(SettingsFactory.DISTANCE_UNITS)).firstOrDefault();
      return DistanceType.provideFromSetting(distanceTypeSetting);
   }
}
