package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import io.techery.mappery.MapperyContext;

public class PhotoSimpleConverter extends PhotoConverter<PhotoSimple> {

   @Override
   public Class<PhotoSimple> sourceClass() {
      return PhotoSimple.class;
   }

   @Override
   public Photo convert(MapperyContext mapperyContext, PhotoSimple apiPhoto) {
      Photo photo = super.convert(mapperyContext, apiPhoto);

      photo.setOwner(mapperyContext.convert(apiPhoto.author(), User.class));

      return photo;
   }
}
