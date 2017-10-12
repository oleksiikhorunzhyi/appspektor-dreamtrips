package com.worldventures.dreamtrips.social.ui.podcast_player.playback;

public interface DtPlayer extends ReadOnlyPlayer {

   void prepare();

   void start();

   void pause();

   void stop();

   void release();

   void seekTo(int position);
}
