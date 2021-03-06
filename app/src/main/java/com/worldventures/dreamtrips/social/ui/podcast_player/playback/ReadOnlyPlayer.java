package com.worldventures.dreamtrips.social.ui.podcast_player.playback;

import android.net.Uri;

import rx.Observable;

public interface ReadOnlyPlayer {

   enum State {
      UNKNOWN,
      PREPARING,
      READY,
      PLAYING,
      PAUSED,
      STOPPED,
      RELEASED,
      ERROR
   }

   DtPlayer.State getState();

   Observable<DtPlayer.State> getStateObservable();

   Uri getSourceUri();

   /**
    * @return duration in millis
    */
   int getDuration();

   /**
    * @return current possition in millis
    */
   int getCurrentPosition();

   boolean isPlaying();

   int getBufferPercentage();
}
