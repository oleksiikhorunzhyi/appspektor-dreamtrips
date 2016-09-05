package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.ImmutableOperationHours;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationHours;

public class OperationHourMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.OperationHours, OperationHours> {

   public static final OperationHourMapper INSTANCE = new OperationHourMapper();

   @Override
   public OperationHours convert(com.worldventures.dreamtrips.api.dtl.merchants.model.OperationHours source) {
      return ImmutableOperationHours.builder()
            .from(source.fromTime())
            .to(source.toTime())
            .build();
   }
}
