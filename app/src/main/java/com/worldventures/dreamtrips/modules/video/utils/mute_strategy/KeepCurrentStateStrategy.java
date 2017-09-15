package com.worldventures.dreamtrips.modules.video.utils.mute_strategy;

public class KeepCurrentStateStrategy implements FullscreenMuteStrategy {

   @Override
   public boolean shouldMute(boolean isFullscreen, boolean currentMuteValue) {
      return currentMuteValue;
   }
}
