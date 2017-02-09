package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocationType;

import io.techery.mappery.MapperyContext;


public class SmartCardLocationToWalletLocationConverter implements Converter<SmartCardLocation, WalletLocation> {
   @Override
   public Class<SmartCardLocation> sourceClass() {
      return SmartCardLocation.class;
   }

   @Override
   public Class<WalletLocation> targetClass() {
      return WalletLocation.class;
   }

   @Override
   public WalletLocation convert(MapperyContext mapperyContext, SmartCardLocation smartCardLocation) {
      return ImmutableWalletLocation.builder()
            .coordinates(mapperyContext.convert(smartCardLocation.coordinates(), WalletCoordinates.class))
            .createdAt(smartCardLocation.createdAt())
            .type(mapperyContext.convert(smartCardLocation.type(), WalletLocationType.class))
            .build();
   }
}
