package com.worldventures.dreamtrips.modules.video.model.converter;


import com.worldventures.dreamtrips.api.member_videos.model.Video;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class VideoConverter implements Converter<Video, com.worldventures.dreamtrips.modules.video.model.Video> {
   @Override
   public Class<Video> sourceClass() {
      return Video.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.modules.video.model.Video> targetClass() {
      return com.worldventures.dreamtrips.modules.video.model.Video.class;
   }

   @Override
   public com.worldventures.dreamtrips.modules.video.model.Video convert(MapperyContext mapperyContext, Video video) {
      return new com.worldventures.dreamtrips.modules.video.model.Video(video.imageUrl(),
            video.videoUrl(),
            video.name(),
            video.category(),
            video.duration());
   }
}
