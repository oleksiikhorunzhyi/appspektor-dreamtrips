package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.post.model.request.Attachment;
import com.worldventures.dreamtrips.api.post.model.request.ImmutablePostData;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.Location;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class ReversePostDataConverter implements Converter<CreatePhotoPostEntity, PostData> {

   @Override
   public PostData convert(MapperyContext mapperyContext, CreatePhotoPostEntity createPhotoPostEntity) {
      ImmutablePostData.Builder postData = ImmutablePostData.builder();

      postData.description(createPhotoPostEntity.getDescription());
      postData.location(mapperyContext.convert(createPhotoPostEntity.getLocation(), Location.class));

      postData.attachments(mapperyContext.convert(createPhotoPostEntity.getAttachments(), Attachment.class));

      return postData.build();
   }

   @Override
   public Class<CreatePhotoPostEntity> sourceClass() {
      return CreatePhotoPostEntity.class;
   }

   @Override
   public Class<PostData> targetClass() {
      return PostData.class;
   }
}
