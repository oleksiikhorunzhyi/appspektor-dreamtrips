package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletVideo;

import io.techery.mappery.MapperyContext;

public class SocialVideoToWalletVideoConverter implements Converter<Video, WalletVideo> {

   @Override
   public WalletVideo convert(MapperyContext mapperyContext, Video video) {
      return new WalletVideo(
            video.getImageUrl(),
            video.getVideoUrl(),
            video.getVideoName(),
            video.getCategory(),
            video.getDuration(),
            video.getLanguage()
      );
   }

   @Override
   public Class<Video> sourceClass() {
      return Video.class;
   }

   @Override
   public Class<WalletVideo> targetClass() {
      return WalletVideo.class;
   }
}
