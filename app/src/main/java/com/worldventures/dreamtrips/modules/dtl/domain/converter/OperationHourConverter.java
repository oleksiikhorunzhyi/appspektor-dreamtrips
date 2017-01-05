package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.ImmutableOperationHours;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationHours;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class OperationHourConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.OperationHours, OperationHours> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.OperationHours> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.OperationHours.class;
   }

   @Override
   public Class<OperationHours> targetClass() {
      return OperationHours.class;
   }

   @Override
   public OperationHours convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.OperationHours operationHours) {
      return ImmutableOperationHours.builder()
            .from(operationHours.fromTime())
            .to(operationHours.toTime())
            .build();
   }
}
