package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;

import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value.Immutable
public abstract class FilterData {

   public static final int LIMIT = 20;

   private static final int BUDGET_MIN = 1;
   private static final int BUDGET_MAX = 5;
   private static final int DISTANCE_MAX_INDEX = 4;
   private static final String RESTAURANT = "restaurant";
   private static final String BAR = "bar";

   @Value.Default
   public int page() {
      return 0;
   }

   @Value.Default
   public int offset() {
      return LIMIT;
   }

   @Value.Default
   public String searchQuery() {
      return "";
   }

   @Value.Default
   public int budgetMin() {
      return BUDGET_MIN;
   }

   @Value.Default
   public int budgetMax() {
      return BUDGET_MAX;
   }

   @Value.Default
   public int distanceMaxIndex() {
      return DISTANCE_MAX_INDEX;
   }

   @Value.Default
   public boolean isOffersOnly() {
      return true;
   }

   @Value @Nullable
   public abstract DistanceType distanceType();

   @Value.Default
   public List<Attribute> selectedAmenities() {
      return Collections.emptyList();
   }

   @Value.Derived
   public boolean isDefault() {
      return budgetMin() == BUDGET_MIN &&
            budgetMax() == BUDGET_MAX &&
            distanceMaxIndex() == DISTANCE_MAX_INDEX &&
            selectedAmenities().isEmpty();
   }

   @Value.Default
   public List<String> getMerchantType() {
      List<String> merchantTypeList = new ArrayList<>();
      merchantTypeList.add(RESTAURANT);
      merchantTypeList.add(BAR);
      return merchantTypeList;
   }
}
