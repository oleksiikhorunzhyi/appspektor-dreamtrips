package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.post.model.request.Attachment;
import com.worldventures.dreamtrips.social.ui.feed.model.CreatePhotoPostEntity;

import io.techery.mappery.MapperyContext;

public class ReversePostAttachmentsConverter implements Converter<CreatePhotoPostEntity.Attachment, Attachment> {

   @Override
   public Class<CreatePhotoPostEntity.Attachment> sourceClass() {
      return CreatePhotoPostEntity.Attachment.class;
   }

   @Override
   public Class<Attachment> targetClass() {
      return Attachment.class;
   }

   @Override
   public Attachment convert(MapperyContext mapperyContext, CreatePhotoPostEntity.Attachment attachment) {
      return Attachment.of(attachment.getUid());
   }
}
