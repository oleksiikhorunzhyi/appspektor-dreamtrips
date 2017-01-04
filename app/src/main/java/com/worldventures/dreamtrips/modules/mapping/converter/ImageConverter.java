package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import io.techery.mappery.MapperyContext;

public class ImageConverter implements Converter<com.worldventures.dreamtrips.api.post.model.response.Image, Image> {

   @Override
   public Image convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.post.model.response.Image apiImage) {
      Image image = new Image();
      image.setUrl(apiImage.url());
      return image;
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.post.model.response.Image> sourceClass() {
      return com.worldventures.dreamtrips.api.post.model.response.Image.class;
   }

   @Override
   public Class<Image> targetClass() {
      return Image.class;
   }
}
