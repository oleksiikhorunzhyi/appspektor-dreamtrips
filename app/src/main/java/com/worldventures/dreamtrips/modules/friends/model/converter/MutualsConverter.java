package com.worldventures.dreamtrips.modules.friends.model.converter;


import com.worldventures.dreamtrips.api.session.model.MutualFriends;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class MutualsConverter implements Converter<MutualFriends, User.MutualFriends> {
   @Override
   public Class<MutualFriends> sourceClass() {
      return MutualFriends.class;
   }

   @Override
   public Class<User.MutualFriends> targetClass() {
      return User.MutualFriends.class;
   }

   @Override
   public User.MutualFriends convert(MapperyContext mapperyContext, MutualFriends mutualFriends) {
      return new User.MutualFriends(mutualFriends.count());
   }
}
