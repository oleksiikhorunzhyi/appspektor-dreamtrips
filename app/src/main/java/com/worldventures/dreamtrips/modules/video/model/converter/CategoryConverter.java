package com.worldventures.dreamtrips.modules.video.model.converter;


import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.video.model.VideoCategory;
import com.worldventures.dreamtrips.modules.video.model.Video;

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
