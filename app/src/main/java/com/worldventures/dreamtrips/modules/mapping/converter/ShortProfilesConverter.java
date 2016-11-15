package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;
import com.worldventures.dreamtrips.modules.common.model.User;

import io.techery.mappery.MapperyContext;

public class ShortProfilesConverter implements Converter<ShortUserProfile, User> {
   @Override
   public Class<ShortUserProfile> sourceClass() {
      return ShortUserProfile.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext context, ShortUserProfile shortUser) {
      User user = new User(shortUser.id());
      user.setUsername(shortUser.username());
      user.setFirstName(shortUser.firstName());
      user.setLastName(shortUser.lastName());
      user.setCompany(shortUser.company());
      User.Avatar avatar = new User.Avatar();
      if (shortUser.avatar() != null) {
         avatar.setThumb(shortUser.avatar().thumb());
         avatar.setMedium(shortUser.avatar().medium());
         avatar.setOriginal(shortUser.avatar().original());
      }
      user.setAvatar(avatar);
      return user;
   }
}
