package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

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
      photo.setUrl(apiPhoto.images().url());
      photo.setTitle(apiPhoto.title());
      photo.setShotAt(apiPhoto.shotAt());
      photo.setCreatedAt(apiPhoto.createdAt());
      photo.setLanguage(apiPhoto.language());

      photo.setLocation(mapperyContext.convert(apiPhoto.location(), Location.class));
      photo.setTags(new ArrayList<>(apiPhoto.tags()));

      if (apiPhoto.width() != null) {
         photo.setWidth(apiPhoto.width());
      }
      if (apiPhoto.height() != null) {
         photo.setHeight(apiPhoto.height());
      }

      photo.setPhotoTagsCount(apiPhoto.photoTagsCount());
      photo.setPhotoTags(mapperyContext.convert(apiPhoto.photoTags(), PhotoTag.class));

      return photo;
   }
}
