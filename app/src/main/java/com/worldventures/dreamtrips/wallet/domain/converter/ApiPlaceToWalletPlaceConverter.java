package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletPlace;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model.ApiPlace;

import io.techery.mappery.MapperyContext;

public class ApiPlaceToWalletPlaceConverter implements Converter<ApiPlace, WalletPlace> {

   @Override
   public WalletPlace convert(MapperyContext mapperyContext, ApiPlace apiPlace) {
      return ImmutableWalletPlace.builder().name(apiPlace.name()).build();
   }

   @Override
   public Class<ApiPlace> sourceClass() {
      return ApiPlace.class;
   }

   @Override
   public Class<WalletPlace> targetClass() {
      return WalletPlace.class;
   }
}
