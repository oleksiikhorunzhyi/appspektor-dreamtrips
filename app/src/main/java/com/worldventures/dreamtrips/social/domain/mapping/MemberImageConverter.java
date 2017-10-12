package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Image;

import io.techery.mappery.MapperyContext;

public class MemberImageConverter implements Converter<com.worldventures.dreamtrips.api.photos.model.Image, Image> {

   @Override
   public Image convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.photos.model.Image apiImage) {
      Image image = new Image();
      image.setUrl(apiImage.url());
      return image;
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.photos.model.Image> sourceClass() {
      return com.worldventures.dreamtrips.api.photos.model.Image.class;
   }

   @Override
   public Class<Image> targetClass() {
      return Image.class;
   }
}
