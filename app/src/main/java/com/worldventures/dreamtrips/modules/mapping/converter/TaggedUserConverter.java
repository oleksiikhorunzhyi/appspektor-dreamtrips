package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.photos.model.TaggedUser;
import com.worldventures.dreamtrips.modules.common.model.User;

import io.techery.mappery.MapperyContext;

public class TaggedUserConverter implements Converter<TaggedUser, User> {

   @Override
   public Class<TaggedUser> sourceClass() {
      return TaggedUser.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext mapperyContext, TaggedUser apiUser) {
      User user = new User();
      user.setId(apiUser.id());
      user.setFirstName(apiUser.firstName());
      user.setLastName(apiUser.lastName());
      user.setUsername(apiUser.username());
      user.setAvatar(mapperyContext.convert(apiUser.avatar(), User.Avatar.class));
      user.setBadges(apiUser.badges());
      user.setLocation(apiUser.location());
      user.setCompany(apiUser.company());
      return user;
   }
}
