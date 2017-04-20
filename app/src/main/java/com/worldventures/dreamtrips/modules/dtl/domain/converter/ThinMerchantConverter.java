package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSummary;
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
            .coordinates(thinMerchant.coordinates() != null ? mapperyContext.convert(thinMerchant.coordinates(), Coordinates.class) : null)
            .city(thinMerchant.city())
            .state(thinMerchant.state())
            .country(thinMerchant.country())
            .budget(thinMerchant.budget())
            .rating(thinMerchant.rating())
            .distance(thinMerchant.distance())
            .timeZone(thinMerchant.timeZone())
            .offers(thinMerchant.offers() != null ? mapperyContext.convert(thinMerchant.offers(), Offer.class) : null)
            .images(thinMerchant.images() != null ? mapperyContext.convert(thinMerchant.images(), MerchantMedia.class) : null)
            .categories(thinMerchant.categories() != null ? mapperyContext.convert(thinMerchant.categories(), ThinAttribute.class) : null)
            .operationDays(thinMerchant.operationDays() != null ? mapperyContext.convert(thinMerchant.operationDays(), OperationDay.class) : null)
            .reviewSummary(thinMerchant.reviewSummary() != null ? mapperyContext.convert(thinMerchant.reviewSummary(), ReviewSummary.class) : null)
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
