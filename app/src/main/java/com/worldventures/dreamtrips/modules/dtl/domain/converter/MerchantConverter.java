package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Reviews;

import io.techery.mappery.MapperyContext;

public class MerchantConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant, Merchant> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant.class;
   }

   @Override
   public Class<Merchant> targetClass() {
      return Merchant.class;
   }

   @Override
   public Merchant convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant merchant) {
      return ImmutableMerchant.builder()
            .id(merchant.id())
            .type(merchant.type())
            .partnerStatus(merchant.partnerStatus())
            .displayName(merchant.displayName())
            .address(merchant.address())
            .city(merchant.city())
            .state(merchant.state())
            .country(merchant.country())
            .description(merchant.description())
            .budget(merchant.budget())
            .distance(merchant.distance())
            .zip(merchant.zip())
            .rating(merchant.rating())
            .phone(merchant.phone())
            .email(merchant.email())
            .website(merchant.website())
            .timeZone(merchant.timeZone())
            .coordinates(merchant.coordinates() != null ? mapperyContext.convert(merchant.coordinates(), Coordinates.class) : null)
            .offers(merchant.offers() != null ? mapperyContext.convert(merchant.offers(), Offer.class) : null)
            .images(merchant.images() != null ? mapperyContext.convert(merchant.images(), MerchantMedia.class) : null)
            .operationDays(merchant.operationDays() != null ? mapperyContext.convert(merchant.operationDays(), OperationDay.class) : null)
            .disclaimers(merchant.disclaimers() != null ? mapperyContext.convert(merchant.disclaimers(), Disclaimer.class) : null)
            .currencies(merchant.currencies() != null ? mapperyContext.convert(merchant.currencies(), Currency.class) : null)
            .categories(merchant.categories() != null ? mapperyContext.convert(merchant.categories(), ThinAttribute.class) : null)
            .amenities(merchant.categories() != null ? mapperyContext.convert(merchant.amenities(), ThinAttribute.class) : null)
            .reviews(merchant.reviews() != null ? mapperyContext.convert(merchant.reviews(), Reviews.class) : null)
            .build();
   }
}
