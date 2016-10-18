package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoAuthor;
import com.worldventures.dreamtrips.api.photos.model.TaggedUser;
import com.worldventures.dreamtrips.modules.common.model.User;

import io.techery.mappery.MapperyContext;

public class PhotoAuthorConverter implements Converter<PhotoAuthor, User> {

   @Override
   public Class<PhotoAuthor> sourceClass() {
      return PhotoAuthor.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext mapperyContext, PhotoAuthor photoAuthor) {
      User user = new User();
      user.setId(photoAuthor.id());
      user.setFirstName(photoAuthor.firstName());
      user.setLastName(photoAuthor.lastName());
      user.setUsername(photoAuthor.username());
      user.setAvatar(mapperyContext.convert(photoAuthor.avatar(), User.Avatar.class));
      user.setLocation(photoAuthor.location());
      user.setCompany(photoAuthor.company());
      return user;
   }
}
