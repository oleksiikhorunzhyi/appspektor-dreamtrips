package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardCoordinates;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;

import io.techery.mappery.MapperyContext;


public class WalletCoordinatesToSmartCardCoordinatesConverter implements Converter<WalletCoordinates, SmartCardCoordinates> {
   @Override
   public Class<WalletCoordinates> sourceClass() {
      return WalletCoordinates.class;
   }

   @Override
   public Class<SmartCardCoordinates> targetClass() {
      return SmartCardCoordinates.class;
   }

   @Override
   public SmartCardCoordinates convert(MapperyContext mapperyContext, WalletCoordinates walletCoordinates) {
      return ImmutableSmartCardCoordinates.builder()
            .lat(walletCoordinates.lat())
            .lng(walletCoordinates.lng())
            .build();
   }
}
