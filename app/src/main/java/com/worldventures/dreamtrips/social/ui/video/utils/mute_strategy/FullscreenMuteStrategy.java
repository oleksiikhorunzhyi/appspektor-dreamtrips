package com.worldventures.dreamtrips.social.ui.video.utils.mute_strategy;

public interface FullscreenMuteStrategy {

   FullscreenMuteStrategy MUTE_IN_WINDOWED_SOUND_IN_FULLSCREEN = new MuteInWindowedStrategy();
   FullscreenMuteStrategy KEEP_CURRENT_VALUE = new KeepCurrentStateStrategy();

   boolean shouldMute(boolean isFullscreen, boolean currentMuteValue);
}
