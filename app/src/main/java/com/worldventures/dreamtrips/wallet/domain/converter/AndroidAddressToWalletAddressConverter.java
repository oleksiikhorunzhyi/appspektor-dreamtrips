package com.worldventures.dreamtrips.wallet.domain.converter;

import android.location.Address;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;

import io.techery.mappery.MapperyContext;


public class AndroidAddressToWalletAddressConverter implements Converter<Address, WalletAddress> {
   @Override
   public Class<Address> sourceClass() {
      return Address.class;
   }

   @Override
   public Class<WalletAddress> targetClass() {
      return WalletAddress.class;
   }

   @Override
   public WalletAddress convert(MapperyContext mapperyContext, Address address) {
      return ImmutableWalletAddress.builder()
            .addressLine(address.getAddressLine(0))
            .countryName(address.getCountryName())
            .adminArea(address.getAdminArea())
            .postalCode(address.getPostalCode())
            .build();
   }
}
