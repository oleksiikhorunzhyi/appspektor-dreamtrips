package com.worldventures.dreamtrips.modules.dtl.helper;

import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;

import java.util.Locale;

public abstract class FilterHelper {

   private static final double MILES_MULTIPLIER = 1.60934;
   private static final double[] MILES_VALUES = {10, 20, 30, 40, 50};
   private static final String DISTANCE_VALUE_FORMAT = "%.0f";

   public static String provideLeftDistanceValueCaption(DistanceType distanceType) {
      return String.format(Locale.US, DISTANCE_VALUE_FORMAT, provideLeftDistanceValue(distanceType));
   }

   public static String provideRightDistanceValueCaption(DistanceType distanceType) {
      return String.format(Locale.US, DISTANCE_VALUE_FORMAT, provideRightDistanceValue(distanceType));
   }

   public static float provideLeftDistanceValue(DistanceType distanceType) {
      return distanceType == DistanceType.KMS ? 15 : 10;
   }

   public static float provideRightDistanceValue(DistanceType distanceType) {
      return distanceType == DistanceType.KMS ? 80 : 50;
   }

   public static double provideDistanceByIndex(DistanceType distanceType, int index) {
      double value = MILES_VALUES[index];
      if (distanceType == DistanceType.KMS) value = value * MILES_MULTIPLIER;
      return value; // TODO :: 26.09.16 limit to one digit
   }

   public static float provideDistancePickerInterval(DistanceType distanceType) {
      return distanceType == DistanceType.KMS ? (float) 16.25 : 10;
   }
}
