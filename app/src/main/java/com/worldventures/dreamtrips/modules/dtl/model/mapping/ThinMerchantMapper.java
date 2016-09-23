package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

public class ThinMerchantMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant, ThinMerchant> {

   public static final ThinMerchantMapper INSTANCE = new ThinMerchantMapper();

   @Override
   public ThinMerchant convert(com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant source) {
      return ImmutableThinMerchant.builder()
            .id(source.id())
            .type(source.type())
            .partnerStatus(source.partnerStatus())
            .displayName(source.displayName())
            .coordinates(CoordinatesMapper.INSTANCE.convert(source.coordinates()))
            .city(source.city())
            .state(source.state())
            .country(source.country())
            .budget(source.budget())
            .rating(source.rating())
            .distance(source.distance())
            .timeZone(source.timeZone())
            .offers(new QueryableMapper<>(OfferMapper.INSTANCE).map(source.offers()))
            .images(new QueryableMapper<>(MerchantMediaMapper.INSTANCE).map(source.images()))
            .categories(new QueryableMapper<>(ThinAttributeMapper.INSTANCE).map(source.categories()))
            .operationDays(new QueryableMapper<>(OperationDayMapper.INSTANCE).map(source.operationDays()))
            .build();
   }
}
