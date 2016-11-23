package com.worldventures.dreamtrips.modules.friends.model.converter;


import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class ApiUserToUserConverter implements Converter<com.worldventures.dreamtrips.api.likes.model.User, User> {
   @Override
   public Class<com.worldventures.dreamtrips.api.likes.model.User> sourceClass() {
      return com.worldventures.dreamtrips.api.likes.model.User.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.likes.model.User apiUser) {
      User user = new User();
      user.setId(apiUser.id());
      user.setFirstName(apiUser.firstName());
      user.setLastName(apiUser.lastName());
      user.setAvatar(mapperyContext.convert(apiUser.avatar(), User.Avatar.class));
      user.setBadges(apiUser.badges());
      user.setLocation(apiUser.location());
      user.setCompany(apiUser.company());
      if (apiUser.mutuals() != null)
         user.setMutualFriends(mapperyContext.convert(apiUser.mutuals(), User.MutualFriends.class));
      user.setRelationship(mapperyContext.convert(apiUser.relationship(), User.Relationship.class));
      return user;
   }
}
