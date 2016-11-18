package com.worldventures.dreamtrips.modules.friends.model.converter;


import com.worldventures.dreamtrips.api.friends.model.FriendProfile;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class FriendProfileToUserConverter implements Converter<FriendProfile, User> {
   @Override
   public Class<FriendProfile> sourceClass() {
      return FriendProfile.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext mapperyContext, FriendProfile apiUser) {
      User user = new User();
      user.setId(apiUser.id());
      user.setFirstName(apiUser.firstName());
      user.setLastName(apiUser.lastName());
      user.setUsername(apiUser.username());
      user.setAvatar(mapperyContext.convert(apiUser.avatar(), User.Avatar.class));
      user.setBadges(apiUser.badges());
      user.setLocation(apiUser.location());
      user.setCompany(apiUser.company());
      if (apiUser.mutuals() != null) user.setMutualFriends(mapperyContext.convert(apiUser.mutuals(), User.MutualFriends.class));
      if (apiUser.relationship() != null)user.setRelationship(mapperyContext.convert(apiUser.relationship(), User.Relationship.class));
      if (apiUser.circles() != null) user.setCircles(mapperyContext.convert(apiUser.circles(), Circle.class));
      return user;
   }
}
