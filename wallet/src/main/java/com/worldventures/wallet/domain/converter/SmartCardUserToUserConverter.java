package com.worldventures.wallet.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.wallet.domain.entity.SmartCardUser;

import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import io.techery.mappery.MapperyContext;

class SmartCardUserToUserConverter implements Converter<SmartCardUser, User> {

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public Class<SmartCardUser> sourceClass() {
      return SmartCardUser.class;
   }


   @Override
   public User convert(MapperyContext mapperyContext, SmartCardUser smartCardUser) {
      return ImmutableUser.builder()
            .firstName(smartCardUser.firstName())
            .middleName(smartCardUser.middleName())
            .lastName(smartCardUser.lastName())
            .build();
   }
}
