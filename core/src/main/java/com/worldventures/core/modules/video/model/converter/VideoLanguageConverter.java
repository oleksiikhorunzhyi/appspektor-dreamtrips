package com.worldventures.core.modules.video.model.converter;


import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.member_videos.model.VideoLanguage;

import io.techery.mappery.MapperyContext;

public class VideoLanguageConverter implements Converter<VideoLanguage, com.worldventures.core.modules.video.model.VideoLanguage> {
   @Override
   public Class<VideoLanguage> sourceClass() {
      return VideoLanguage.class;
   }

   @Override
   public Class<com.worldventures.core.modules.video.model.VideoLanguage> targetClass() {
      return com.worldventures.core.modules.video.model.VideoLanguage.class;
   }

   @Override
   public com.worldventures.core.modules.video.model.VideoLanguage convert(MapperyContext mapperyContext, VideoLanguage videoLanguage) {
      return new com.worldventures.core.modules.video.model.VideoLanguage(videoLanguage.title(), videoLanguage.localeName());
   }
}
