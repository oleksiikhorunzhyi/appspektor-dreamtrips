package com.worldventures.dreamtrips.modules.mapping.mapper;

import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.Mapper;

import javax.inject.Singleton;

@Singleton
public class ShortProfilesMapper implements Mapper<ShortUserProfile, User> {

   @Override
   public User map(ShortUserProfile shortUser) {
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

