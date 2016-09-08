package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.ImmutableOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

public class OfferMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Offer, Offer> {

   public static final OfferMapper INSTANCE = new OfferMapper();

   @Override
   public Offer convert(com.worldventures.dreamtrips.api.dtl.merchants.model.Offer source) {
      return ImmutableOffer.builder()
            .id(source.id())
            .type(source.type())
            .title(source.title())
            .description(source.description())
            .disclaimer(source.disclaimer())
            .startDate(source.startDate())
            .endDate(source.endDate())
            .images(new QueryableMapper<>(MerchantMediaMapper.INSTANCE).map(source.images()))
            .operationDays(new QueryableMapper<>(OperationDayMapper.INSTANCE).map(source.operationDays()))
            .build();
   }
}
