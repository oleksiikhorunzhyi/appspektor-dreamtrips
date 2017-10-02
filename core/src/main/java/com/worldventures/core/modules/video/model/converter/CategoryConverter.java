package com.worldventures.core.modules.video.model.converter;


import com.worldventures.core.converter.Converter;
import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.modules.video.model.VideoCategory;

import io.techery.mappery.MapperyContext;

public class CategoryConverter implements Converter<com.worldventures.dreamtrips.api.member_videos.model.VideoCategory, VideoCategory> {
   @Override
   public Class<com.worldventures.dreamtrips.api.member_videos.model.VideoCategory> sourceClass() {
      return com.worldventures.dreamtrips.api.member_videos.model.VideoCategory.class;
   }

   @Override
   public Class<VideoCategory> targetClass() {
      return VideoCategory.class;
   }

   @Override
   public VideoCategory convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.member_videos.model.VideoCategory videoCategory) {
      return new VideoCategory(videoCategory.title(), mapperyContext.convert(videoCategory.videos(), Video.class));
   }
}
