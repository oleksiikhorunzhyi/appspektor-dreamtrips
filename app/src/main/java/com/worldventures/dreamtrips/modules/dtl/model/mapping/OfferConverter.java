package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.ImmutableOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

import io.techery.mappery.MapperyContext;

public class OfferConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Offer, Offer> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.Offer> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.Offer.class;
   }

   @Override
   public Class<Offer> targetClass() {
      return Offer.class;
   }

   @Override
   public Offer convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.Offer offer) {
      return ImmutableOffer.builder()
            .id(offer.id())
            .type(offer.type())
            .title(offer.title())
            .description(offer.description())
            .disclaimer(offer.disclaimer())
            .startDate(offer.startDate())
            .endDate(offer.endDate())
            .images(mapperyContext.convert(offer.images(), MerchantMedia.class))
            .build();
   }
}
