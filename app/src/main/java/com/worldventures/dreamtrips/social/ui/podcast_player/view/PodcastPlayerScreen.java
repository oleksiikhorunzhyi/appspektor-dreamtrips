package com.worldventures.dreamtrips.social.ui.podcast_player.view;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface PodcastPlayerScreen extends DtlScreen {

   void setProgress(int duration, int currentPosition, int bufferPercentage);

   void setPausePlay(boolean isPlaying);

   void setPlaybackFailed();

   void setPreparing();
}
