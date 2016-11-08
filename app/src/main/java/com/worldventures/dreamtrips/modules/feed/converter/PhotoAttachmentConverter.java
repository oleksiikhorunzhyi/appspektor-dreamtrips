package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import io.techery.mappery.MapperyContext;

public class PhotoAttachmentConverter extends PhotoConverter<PhotoAttachment> {

   @Override
   public Class<PhotoAttachment> sourceClass() {
      return PhotoAttachment.class;
   }

   @Override
   public Photo convert(MapperyContext mapperyContext, PhotoAttachment apiPhoto) {
      return super.convert(mapperyContext, apiPhoto);
   }
}
