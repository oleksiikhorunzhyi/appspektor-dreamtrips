package com.worldventures.dreamtrips.social.ui.friends.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate;

import io.techery.mappery.MapperyContext;

public class FriendCandidateToUserConverter implements Converter<FriendCandidate, User> {
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
      user.setRelationship(mapperyContext.convert(apiUser.relationship(), User.Relationship.class));
      user.setLocation(apiUser.location());
      user.setCompany(apiUser.company());
      user.setMutualFriends(mapperyContext.convert(apiUser.mutuals(), User.MutualFriends.class));
      return user;
   }
}
