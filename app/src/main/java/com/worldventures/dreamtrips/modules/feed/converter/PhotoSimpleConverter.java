package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

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
