package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.post.model.request.Attachment;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

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
