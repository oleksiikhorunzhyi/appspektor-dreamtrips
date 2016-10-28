package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

import io.techery.mappery.MapperyContext;

public class SimplePhotoConverter implements Converter<PhotoSimple, Photo> {

   @Override
   public Photo convert(MapperyContext mapperyContext, PhotoSimple apiPhoto) {
      Photo photo = new Photo(apiPhoto.uid());
      photo.setTitle(apiPhoto.title());
      photo.setShotAt(apiPhoto.shotAt());
      Location coordinates = mapperyContext.convert(apiPhoto.location(), Location.class);
      photo.setCoordinates(coordinates);
      photo.setTags(new ArrayList<>(apiPhoto.tags()));
      com.worldventures.dreamtrips.api.photos.model.Image apiImage = apiPhoto.images();
      Image image = mapperyContext.convert(apiImage, Image.class);
      photo.setImages(image);
      if (apiPhoto.width() != null) photo.setWidth(apiPhoto.width());
      if (apiPhoto.height() != null) photo.setHeight(apiPhoto.height());

      photo.setPhotoTagsCount(apiPhoto.photoTagsCount());
      photo.setPhotoTags(mapperyContext.convert(apiPhoto.photoTags(), PhotoTag.class));

      photo.setUser(mapperyContext.convert(apiPhoto.author(), User.class));

      return photo;
   }

   @Override
   public Class<PhotoSimple> sourceClass() {
      return PhotoSimple.class;
   }

   @Override
   public Class<Photo> targetClass() {
      return Photo.class;
   }
}
