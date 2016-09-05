package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.ImmutableOperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

public class OperationDayMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.OperationDay, OperationDay> {

   public static final OperationDayMapper INSTANCE = new OperationDayMapper();

   @Override
   public OperationDay convert(com.worldventures.dreamtrips.api.dtl.merchants.model.OperationDay source) {
      return ImmutableOperationDay.builder()
            .dayOfWeek(DayOfWeek.from(source.dayOfWeek()))
            .operationHours(new QueryableMapper<>(OperationHourMapper.INSTANCE).map(source.operationHours()))
            .build();
   }
}
