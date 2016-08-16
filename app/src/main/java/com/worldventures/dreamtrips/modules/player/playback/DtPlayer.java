package com.worldventures.dreamtrips.modules.player.playback;

public interface DtPlayer extends ReadOnlyPlayer {

    void prepare();

    void start();

    void pause();

    void stop();

    void release();

    void seekTo(int position);
}
