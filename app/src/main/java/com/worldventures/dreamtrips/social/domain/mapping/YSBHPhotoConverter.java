package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;

import io.techery.mappery.MapperyContext;

public class YSBHPhotoConverter implements Converter<com.worldventures.dreamtrips.api.ysbh.model.YSBHPhoto, YSBHPhoto> {

   @Override
   public YSBHPhoto convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.ysbh.model.YSBHPhoto apiPhoto) {
      return new YSBHPhoto(apiPhoto.id(), apiPhoto.image().url(), apiPhoto.title());
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.ysbh.model.YSBHPhoto> sourceClass() {
      return com.worldventures.dreamtrips.api.ysbh.model.YSBHPhoto.class;
   }

   @Override
   public Class<YSBHPhoto> targetClass() {
      return YSBHPhoto.class;
   }
}
