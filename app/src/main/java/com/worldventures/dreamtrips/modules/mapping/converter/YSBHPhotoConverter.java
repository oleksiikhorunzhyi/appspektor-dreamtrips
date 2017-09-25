package com.worldventures.dreamtrips.modules.mapping.converter;


import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;

import io.techery.mappery.MapperyContext;

public class YSBHPhotoConverter implements Converter<com.worldventures.dreamtrips.api.ysbh.model.YSBHPhoto, YSBHPhoto> {

   @Override
   public YSBHPhoto convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.ysbh.model.YSBHPhoto apiPhoto) {
      YSBHPhoto ysbhPhoto = new YSBHPhoto();
      ysbhPhoto.setId(apiPhoto.id());
      ysbhPhoto.setTitle(apiPhoto.title());
      ysbhPhoto.setUrl(apiPhoto.image().url());
      return ysbhPhoto;
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
