package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import io.techery.mappery.MapperyContext;

public class ThinMerchantConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant, ThinMerchant> {

   @Override
   public ThinMerchant convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant thinMerchant) {
      return ImmutableThinMerchant.builder()
            .id(thinMerchant.id())
            .type(thinMerchant.type())
            .partnerStatus(thinMerchant.partnerStatus())
            .displayName(thinMerchant.displayName())
            .coordinates(mapperyContext.convert(thinMerchant.coordinates(), Coordinates.class))
            .city(thinMerchant.city())
            .state(thinMerchant.state())
            .country(thinMerchant.country())
            .budget(thinMerchant.budget())
            .rating(thinMerchant.rating())
            .distance(thinMerchant.distance())
            .timeZone(thinMerchant.timeZone())
            .offers(mapperyContext.convert(thinMerchant.offers(), Offer.class))
            .images(mapperyContext.convert(thinMerchant.images(), MerchantMedia.class))
            .categories(mapperyContext.convert(thinMerchant.categories(), ThinAttribute.class))
            .operationDays(mapperyContext.convert(thinMerchant.operationDays(), OperationDay.class))
            .build();
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant.class;
   }

   @Override
   public Class<ThinMerchant> targetClass() {
      return ThinMerchant.class;
   }
}
