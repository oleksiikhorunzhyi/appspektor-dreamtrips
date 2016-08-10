package com.worldventures.dreamtrips.modules.player.playback;

import android.net.Uri;
import android.widget.MediaController;

import rx.Observable;

public interface DtPlayer {

    enum State {
        UNKNOWN,
        PREPARING,
        READY,
        PLAYING,
        PAUSED,
        STOPPED,
        ERROR
    }

    void prepare();

    void start();

    void pause();

    void release();

    State getState();

    Observable<State> getStateObservable();

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

    MediaController.MediaPlayerControl getMediaPlayerControl();
}
