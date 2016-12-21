package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.profile.model.ProfileAddress;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;

import io.techery.mappery.MapperyContext;

class ProfileAddressToUserAddressConverter implements Converter<ProfileAddress, AddressInfo> {

   @Override
   public Class<ProfileAddress> sourceClass() {
      return ProfileAddress.class;
   }

   @Override
   public Class<AddressInfo> targetClass() {
      return AddressInfo.class;
   }

   @Override
   public AddressInfo convert(MapperyContext mapperyContext, ProfileAddress profileAddress) {
      return ImmutableAddressInfo.builder()
            .address1(profileAddress.address1())
            .address2(profileAddress.address2())
            .city(profileAddress.city())
            .state(profileAddress.state())
            .zip(profileAddress.zipCode())
            .build();
   }
}