package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.api.session.model.Avatar;

import io.techery.mappery.MapperyContext;

public class UserAvatarConverter implements Converter<Avatar, User.Avatar> {
   @Override
   public Class<Avatar> sourceClass() {
      return Avatar.class;
   }

   @Override
   public Class<User.Avatar> targetClass() {
      return User.Avatar.class;
   }

   @Override
   public User.Avatar convert(MapperyContext mapperyContext, Avatar avatar) {
      User.Avatar ava = new User.Avatar();
      ava.setThumb(avatar.thumb());
      ava.setMedium(avatar.medium());
      ava.setOriginal(avatar.original());
      return ava;
   }
}
