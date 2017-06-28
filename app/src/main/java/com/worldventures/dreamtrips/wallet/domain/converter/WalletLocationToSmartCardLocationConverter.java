package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;

import io.techery.mappery.MapperyContext;


public class WalletLocationToSmartCardLocationConverter implements Converter<WalletLocation, SmartCardLocation> {
   @Override
   public Class<WalletLocation> sourceClass() {
      return WalletLocation.class;
   }

   @Override
   public Class<SmartCardLocation> targetClass() {
      return SmartCardLocation.class;
   }

   @Override
   public SmartCardLocation convert(MapperyContext mapperyContext, WalletLocation walletLocation) {
      return ImmutableSmartCardLocation.builder()
            .coordinates(mapperyContext.convert(walletLocation.coordinates(), SmartCardCoordinates.class))
            .createdAt(walletLocation.createdAt())
            .type(mapperyContext.convert(walletLocation.type(), SmartCardLocationType.class))
            .build();
   }
}
