package com.worldventures.dreamtrips.modules.dtl.helper;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public abstract class FilterHelper {

   public static final double MILES_MULTIPLIER = 1.60934;

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

   @SuppressWarnings("PMD.AvoidDecimalLiteralsInBigDecimalConstructor")
   public static double provideMaxDistance(FilterData filterData) {
      double value = MILES_VALUES[filterData.distanceMaxIndex()] * MILES_MULTIPLIER;
      return new BigDecimal(value).setScale(1, RoundingMode.DOWN).doubleValue();
   }

   public static float provideDistancePickerInterval(FilterData filterData) {
      return filterData.distanceType() == DistanceType.KMS ? (float) 16 : 10;
   }

   public static DistanceType provideDistanceFromSettings(SettingsStorage settingsStorage) {
      final Setting distanceTypeSetting = Queryable.from(settingsStorage.getSettings())
            .filter(setting -> setting.getName().equals(SettingsFactory.DISTANCE_UNITS)).firstOrDefault();
      return DistanceType.provideFromSetting(distanceTypeSetting);
   }
}
