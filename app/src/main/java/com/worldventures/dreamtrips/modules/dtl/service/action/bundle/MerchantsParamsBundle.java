package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

public final class MerchantsParamsBundle {

   private final FilterData filterData;
   private final DtlLocation location;
   private final RequestSourceType requestSourceType;

   public static MerchantsParamsBundle create(FilterData filterData, DtlLocation location, RequestSourceType requestSourceType) {
      return new MerchantsParamsBundle(filterData, location, requestSourceType);
   }

   public MerchantsParamsBundle(FilterData filterData, DtlLocation location, RequestSourceType requestSourceType) {
      this.filterData = filterData;
      this.location = location;
      this.requestSourceType = requestSourceType;
   }

   public FilterData filterData() {
      return filterData;
   }

   public DtlLocation location() {
      return location;
   }

   public RequestSourceType requestSource() {
      return requestSourceType;
   }
}
