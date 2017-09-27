package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoAttachment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

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
