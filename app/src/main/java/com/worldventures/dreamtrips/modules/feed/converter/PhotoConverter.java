package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

import io.techery.mappery.MapperyContext;

public abstract class PhotoConverter<T extends com.worldventures.dreamtrips.api.photos.model.Photo>
      implements Converter<T, Photo> {

   @Override
   public Class<Photo> targetClass() {
      return Photo.class;
   }

   @Override
   public Photo convert(MapperyContext mapperyContext, T apiPhoto) {
      Photo photo = new Photo();
      photo.setUid(apiPhoto.uid());
      photo.setImages(mapperyContext.convert(apiPhoto.images(), Image.class));
      photo.setTitle(apiPhoto.title());
      photo.setShotAt(apiPhoto.shotAt());

      photo.setLocation(mapperyContext.convert(apiPhoto.location(), Location.class));
      photo.setTags(new ArrayList<>(apiPhoto.tags()));

      if (apiPhoto.width() != null) photo.setWidth(apiPhoto.width());
      if (apiPhoto.height() != null) photo.setHeight(apiPhoto.height());

      photo.setPhotoTagsCount(apiPhoto.photoTagsCount());
      photo.setPhotoTags(mapperyContext.convert(apiPhoto.photoTags(), PhotoTag.class));

      return photo;
   }
}
