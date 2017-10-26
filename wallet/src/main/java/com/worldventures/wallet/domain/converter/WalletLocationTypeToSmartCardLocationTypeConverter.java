package com.worldventures.wallet.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType;

import io.techery.mappery.MapperyContext;


public class WalletLocationTypeToSmartCardLocationTypeConverter implements Converter<WalletLocationType, SmartCardLocationType> {
   @Override
   public Class<WalletLocationType> sourceClass() {
      return WalletLocationType.class;
   }

   @Override
   public Class<SmartCardLocationType> targetClass() {
      return SmartCardLocationType.class;
   }

   @Override
   public SmartCardLocationType convert(MapperyContext mapperyContext, WalletLocationType walletLocationType) {
      SmartCardLocationType smartCardLocationType = null;
      switch (walletLocationType) {
         case CONNECT:
            smartCardLocationType = SmartCardLocationType.CONNECT;
            break;
         case DISCONNECT:
            smartCardLocationType = SmartCardLocationType.DISCONNECT;
            break;
      }
      return smartCardLocationType;
   }
}
