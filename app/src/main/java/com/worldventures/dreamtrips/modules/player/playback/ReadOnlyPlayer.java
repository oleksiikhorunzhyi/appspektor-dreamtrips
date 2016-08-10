package com.worldventures.dreamtrips.modules.player.playback;

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
}
