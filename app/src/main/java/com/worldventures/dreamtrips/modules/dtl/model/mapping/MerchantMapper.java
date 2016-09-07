package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

public class MerchantMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant, Merchant> {

   public static final MerchantMapper INSTANCE = new MerchantMapper();

   @Override
   public Merchant convert(com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant source) {
      return ImmutableMerchant.builder()
            .id(source.id())
            .type(source.type())
            .partnerStatus(source.partnerStatus())
            .displayName(source.displayName())
            .address(source.address())
            .city(source.city())
            .state(source.state())
            .country(source.country())
            .description(source.description())
            .budget(source.budget())
            .distance(source.distance())
            .zip(source.zip())
            .rating(source.rating())
            .phone(source.phone())
            .email(source.email())
            .website(source.website())
            .timeZone(source.timeZone())
            .coordinates(CoordinatesMapper.INSTANCE.convert(source.coordinates()))
            .offers(new QueryableMapper<>(OfferMapper.INSTANCE).map(source.offers()))
            .images(new QueryableMapper<>(MerchantMediaMapper.INSTANCE).map(source.images()))
            .operationDays(new QueryableMapper<>(OperationDayMapper.INSTANCE).map(source.operationDays()))
            .disclaimers(new QueryableMapper<>(DisclaimerMapper.INSTANCE).map(source.disclaimers()))
            .currencies(new QueryableMapper<>(CurrencyMapper.INSTANCE).map(source.currencies()))
            .categories(new QueryableMapper<>(ThinAttributeMapper.INSTANCE).map(source.categories()))
            .amenities(new QueryableMapper<>(ThinAttributeMapper.INSTANCE).map(source.amenities()))
            .build();
   }
}
