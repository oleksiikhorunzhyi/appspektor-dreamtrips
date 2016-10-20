package com.worldventures.dreamtrips.modules.friends.model.converter;


import com.worldventures.dreamtrips.api.friends.model.FriendCandidate;
import com.worldventures.dreamtrips.api.friends.model.FriendProfile;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class FriendСandidateToUserConverter implements Converter<FriendCandidate, User> {
   @Override
   public Class<FriendCandidate> sourceClass() {
      return FriendCandidate.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext mapperyContext, FriendCandidate apiUser) {
      User user = new User();
      user.setId(apiUser.id());
      user.setFirstName(apiUser.firstName());
      user.setLastName(apiUser.lastName());
      user.setUsername(apiUser.username());
      user.setAvatar(mapperyContext.convert(apiUser.avatar(), User.Avatar.class));
      user.setBadges(apiUser.badges());
      user.setLocation(apiUser.location());
      user.setCompany(apiUser.company());
      user.setMutualFriends(mapperyContext.convert(apiUser.mutuals(), User.MutualFriends.class));
      return user;
   }
}
