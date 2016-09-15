package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;

import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
public abstract class FilterData {

   private static final int BUDGET_MIN = 1;
   private static final int BUDGET_MAX = 5;
   private static final int DISTANCE_MAX_INDEX = 5;

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
      return false;
   }

   @Value.Default
   public List<Attribute> selectedAmenities() {
      return Collections.emptyList();
   };

   @Value.Derived
   public boolean isDefault() {
      return budgetMin() == BUDGET_MIN &&
            budgetMax() == BUDGET_MAX &&
            distanceMaxIndex() == DISTANCE_MAX_INDEX &&
            !isOffersOnly() &&
            selectedAmenities().isEmpty();
   }
}
