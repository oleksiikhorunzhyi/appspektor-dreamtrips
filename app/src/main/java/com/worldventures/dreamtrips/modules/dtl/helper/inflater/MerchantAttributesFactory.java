package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

public class MerchantAttributesFactory {

   private MerchantAttributesFactory(){}

   public static MerchantAttributes create(Merchant merchant) {
      return ImmutableMerchantAttributes.builder()
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
            .coordinates(merchant.coordinates())
            .offers(merchant.offers())
            .images(merchant.images())
            .operationDays(merchant.operationDays())
            .disclaimers(merchant.disclaimers())
            .currencies(merchant.currencies())
            .categories(merchant.categories())
            .amenities(merchant.amenities())
            .build();
   }

   public static MerchantAttributes create(ThinMerchant merchant) {
      return ImmutableMerchantAttributes.builder()
            .id(merchant.id())
            .type(merchant.type())
            .partnerStatus(merchant.partnerStatus())
            .displayName(merchant.displayName())
            .city(merchant.city())
            .state(merchant.state())
            .country(merchant.country())
            .budget(merchant.budget())
            .distance(merchant.distance())
            .rating(merchant.rating())
            .timeZone(merchant.timeZone())
            .coordinates(merchant.coordinates())
            .offers(merchant.offers())
            .images(merchant.images())
            .operationDays(merchant.operationDays())
            .categories(merchant.categories())
            .build();
   }

}
