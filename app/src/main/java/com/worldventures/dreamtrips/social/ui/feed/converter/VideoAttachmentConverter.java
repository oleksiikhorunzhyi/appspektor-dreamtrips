package com.worldventures.dreamtrips.social.ui.feed.converter;

import com.worldventures.dreamtrips.api.feed.model.VideoAttachment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.converter.BaseVideoConverter;

public class VideoAttachmentConverter extends BaseVideoConverter<VideoAttachment> {

   @Override
   public Class<VideoAttachment> sourceClass() {
      return VideoAttachment.class;
   }
}
