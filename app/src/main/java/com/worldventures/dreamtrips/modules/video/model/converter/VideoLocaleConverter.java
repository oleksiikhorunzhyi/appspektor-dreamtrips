package com.worldventures.dreamtrips.modules.video.model.converter;


import com.worldventures.dreamtrips.api.member_videos.model.VideoLocale;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;

import io.techery.mappery.MapperyContext;

public class VideoLocaleConverter implements Converter<VideoLocale, com.worldventures.dreamtrips.modules.video.model.VideoLocale> {
   @Override
   public Class<VideoLocale> sourceClass() {
      return VideoLocale.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.modules.video.model.VideoLocale> targetClass() {
      return com.worldventures.dreamtrips.modules.video.model.VideoLocale.class;
   }

   @Override
   public com.worldventures.dreamtrips.modules.video.model.VideoLocale convert(MapperyContext mapperyContext, VideoLocale videoLocale) {
      return new com.worldventures.dreamtrips.modules.video.model.VideoLocale(videoLocale.title(),
            videoLocale.country(),
            videoLocale.iconUrl(),
            mapperyContext.convert(videoLocale.languages(), VideoLanguage.class));
   }
}
