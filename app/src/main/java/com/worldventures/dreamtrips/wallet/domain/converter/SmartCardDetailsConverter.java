package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;

import io.techery.mappery.MapperyContext;

class SmartCardDetailsConverter implements Converter<com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails, SmartCardDetails> {

   @Override
   public Class<SmartCardDetails> targetClass() {
      return SmartCardDetails.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails> sourceClass() {
      return com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails.class;
   }

   @Override
   public SmartCardDetails convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails smartCardDetails) {
      return ImmutableSmartCardDetails.builder()
            .serialNumber(smartCardDetails.serialNumber())
            .smartCardId(smartCardDetails.scID())
            .bleAddress(smartCardDetails.bleAddress())
            .wvOrderId(smartCardDetails.wvOrderId())
            .revVersion(smartCardDetails.revVersion())
            .nxtOrderId(smartCardDetails.nxtOrderId())
            .orderDate(smartCardDetails.orderDate())
            .build();
   }
}
