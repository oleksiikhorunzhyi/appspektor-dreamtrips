package com.worldventures.dreamtrips.social.ui.video.utils.mute_strategy;

public class MuteInWindowedStrategy implements FullscreenMuteStrategy {

   @Override
   public boolean shouldMute(boolean isFullscreen, boolean currentMuteValue) {
      return !isFullscreen;
   }
}
