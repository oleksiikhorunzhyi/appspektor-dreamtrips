package com.worldventures.dreamtrips.modules.video.model.converter;


import com.worldventures.dreamtrips.api.member_videos.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class VideoLanguageConverter implements Converter<VideoLanguage, com.worldventures.dreamtrips.modules.video.model.VideoLanguage> {
   @Override
   public Class<VideoLanguage> sourceClass() {
      return VideoLanguage.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.modules.video.model.VideoLanguage> targetClass() {
      return com.worldventures.dreamtrips.modules.video.model.VideoLanguage.class;
   }

   @Override
   public com.worldventures.dreamtrips.modules.video.model.VideoLanguage convert(MapperyContext mapperyContext, VideoLanguage videoLanguage) {
      return new com.worldventures.dreamtrips.modules.video.model.VideoLanguage(videoLanguage.title(), videoLanguage.localeName());
   }
}
