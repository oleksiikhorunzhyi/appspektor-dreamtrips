package com.worldventures.dreamtrips.modules.dtl.helper.comparator;


import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.Comparator;

public final class LocationComparator implements Comparator<DtlLocation> {

   public final static Comparator<DtlLocation> CATEGORY_COMPARATOR = (lhs, rhs) -> lhs.type().ordinal() - rhs.type()
         .ordinal();

   public final static Comparator<DtlLocation> ALPHABETICAL_COMPARATOR = (lhs, rhs) -> lhs.longName()
         .compareToIgnoreCase(rhs.longName());

   private final String subString;

   public static Comparator<DtlLocation> provideComparator(String query) {
      return new LocationComparator(query);
   }

   private LocationComparator(String subString) {
      this.subString = subString;
   }

   @Override
   public int compare(DtlLocation lhs, DtlLocation rhs) {
      int rangeSortResult = ProjectTextUtils.substringLocation(lhs.longName(), subString) - ProjectTextUtils.substringLocation(rhs
            .longName(), subString);
      if (rangeSortResult != 0) {
         return rangeSortResult;
      } else {
         return ALPHABETICAL_COMPARATOR.compare(lhs, rhs);
      }
   }
}
