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

    void start();

    void pause();

    void release();

    State getState();

    Observable<State> getStateObservable();

    Uri getSourceUri();

    MediaController.MediaPlayerControl getMediaPlayerControl();
}
