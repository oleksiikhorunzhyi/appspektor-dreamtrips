package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import io.techery.mappery.MapperyContext;

public class SmartCardUserToUserConverter implements Converter<SmartCardUser, User> {

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
