package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardInfo;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import io.techery.mappery.MapperyContext;

// TODO: 2/21/17 remove converter
class SmartCardInfoToSmartCard implements Converter<SmartCardInfo, SmartCard> {
   @Override
   public Class<SmartCardInfo> sourceClass() {
      return SmartCardInfo.class;
   }

   @Override
   public Class<SmartCard> targetClass() {
      return SmartCard.class;
   }

   @Override
   public SmartCard convert(MapperyContext mapperyContext, SmartCardInfo smartCardInfo) {
      return ImmutableSmartCard.builder()
            .smartCardId(String.valueOf(smartCardInfo.scId()))
            // TODO: 2/21/17
//            .user(ImmutableSmartCardUser.builder()
//                  .firstName(smartCardInfo.user().firstName())
//                  .middleName(smartCardInfo.user().middleName() != null ? smartCardInfo.user().middleName() : "")
//                  .lastName(smartCardInfo.user().lastName() != null ? smartCardInfo.user().lastName() : "")
//                  .build())
            .serialNumber(smartCardInfo.serialNumber())
            .deviceAddress(smartCardInfo.bleAddress())
            .cardStatus(SmartCard.CardStatus.ACTIVE)
            .deviceId(smartCardInfo.deviceId())
            .build();
   }
}
