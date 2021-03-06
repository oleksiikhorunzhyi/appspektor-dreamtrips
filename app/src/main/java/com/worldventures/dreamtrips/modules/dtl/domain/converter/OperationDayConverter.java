package com.worldventures.dreamtrips.modules.dtl.domain.converter;


import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.ImmutableOperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationHours;

import io.techery.mappery.MapperyContext;

public class OperationDayConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.OperationDay, OperationDay> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.OperationDay> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.OperationDay.class;
   }

   @Override
   public Class<OperationDay> targetClass() {
      return OperationDay.class;
   }

   @Override
   public OperationDay convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.OperationDay operationDay) {
      return ImmutableOperationDay.builder()
            .dayOfWeek(DayOfWeek.from(operationDay.dayOfWeek()))
            .operationHours(operationDay.operationHours() != null ? mapperyContext.convert(operationDay.operationHours(), OperationHours.class) : null)
            .build();
   }
}
