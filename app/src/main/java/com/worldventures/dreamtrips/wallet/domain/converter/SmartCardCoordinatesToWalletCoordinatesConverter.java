package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;

import io.techery.mappery.MapperyContext;

public class SmartCardCoordinatesToWalletCoordinatesConverter implements Converter<SmartCardCoordinates, WalletCoordinates> {
   @Override
   public Class<SmartCardCoordinates> sourceClass() {
      return SmartCardCoordinates.class;
   }

   @Override
   public Class<WalletCoordinates> targetClass() {
      return WalletCoordinates.class;
   }

   @Override
   public WalletCoordinates convert(MapperyContext mapperyContext, SmartCardCoordinates smartCardCoordinates) {
      return ImmutableWalletCoordinates.builder()
            .lat(smartCardCoordinates.lat())
            .lng(smartCardCoordinates.lng())
            .build();
   }
}
