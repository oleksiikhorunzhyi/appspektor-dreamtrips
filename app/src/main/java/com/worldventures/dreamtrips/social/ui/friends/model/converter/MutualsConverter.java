package com.worldventures.dreamtrips.social.ui.friends.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.api.session.model.MutualFriends;

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
