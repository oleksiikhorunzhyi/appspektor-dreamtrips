package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardInfo;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;

import io.techery.mappery.MapperyContext;

class SmartCardInfoToSmartCardDetail implements Converter<SmartCardInfo, SmartCardDetails> {

   @Override
   public Class<SmartCardInfo> sourceClass() {
      return SmartCardInfo.class;
   }

   @Override
   public Class<SmartCardDetails> targetClass() {
      return SmartCardDetails.class;
   }

   @Override
   public SmartCardDetails convert(MapperyContext mapperyContext, SmartCardInfo smartCardInfo) {
      return ImmutableSmartCardDetails.builder()
            .smartCardId(smartCardInfo.scId())
            .serialNumber(smartCardInfo.serialNumber())
            .bleAddress(smartCardInfo.bleAddress())
            .revVersion(smartCardInfo.revVersion())
            .wvOrderId(smartCardInfo.wvOrderId())
            .nxtOrderId(smartCardInfo.nxtOrderId())
            .orderDate(smartCardInfo.orderDate())
            .build();
   }
}
