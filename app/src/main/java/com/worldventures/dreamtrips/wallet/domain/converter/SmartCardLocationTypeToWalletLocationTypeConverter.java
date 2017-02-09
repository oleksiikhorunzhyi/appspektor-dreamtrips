package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocationType;

import io.techery.mappery.MapperyContext;

public class SmartCardLocationTypeToWalletLocationTypeConverter implements Converter<SmartCardLocationType, WalletLocationType> {
   @Override
   public Class<SmartCardLocationType> sourceClass() {
      return SmartCardLocationType.class;
   }

   @Override
   public Class<WalletLocationType> targetClass() {
      return WalletLocationType.class;
   }

   @Override
   public WalletLocationType convert(MapperyContext mapperyContext, SmartCardLocationType locationType) {
      WalletLocationType walletLocationType = null;
      switch (locationType) {
         case CONNECT:
            walletLocationType = WalletLocationType.CONNECT;
            break;
         case DISCONNECT:
            walletLocationType = WalletLocationType.DISCONNECT;
      }
      return walletLocationType;
   }
}
