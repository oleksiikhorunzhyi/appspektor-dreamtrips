package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

import io.techery.janet.smartcard.model.User;
import io.techery.mappery.MapperyContext;

public class UserToSmartCardUserConverter implements Converter<User, SmartCardUser> {

   @Override
   public Class<User> sourceClass() {
      return User.class;
   }

   @Override
   public Class<SmartCardUser> targetClass() {
      return SmartCardUser.class;
   }

   @Override
   public SmartCardUser convert(MapperyContext mapperyContext, User user) {
      return ImmutableSmartCardUser.builder()
            .firstName(user.firstName())
            .middleName(user.middleName() == null? "" : user.middleName())
            .lastName(user.lastName())
            .build();
   }
}
