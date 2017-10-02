package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.post.model.request.Attachment;
import com.worldventures.dreamtrips.api.post.model.request.ImmutablePostData;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.Location;
import com.worldventures.dreamtrips.social.ui.feed.model.CreatePhotoPostEntity;

import io.techery.mappery.MapperyContext;

public class ReversePostDataConverter implements Converter<CreatePhotoPostEntity, PostData> {

   @Override
   public PostData convert(MapperyContext mapperyContext, CreatePhotoPostEntity createPhotoPostEntity) {
      ImmutablePostData.Builder postData = ImmutablePostData.builder();

      postData.description(createPhotoPostEntity.getDescription());
      if (createPhotoPostEntity.getLocation() != null) {
         postData.location(mapperyContext.convert(createPhotoPostEntity.getLocation(), Location.class));
      }

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
