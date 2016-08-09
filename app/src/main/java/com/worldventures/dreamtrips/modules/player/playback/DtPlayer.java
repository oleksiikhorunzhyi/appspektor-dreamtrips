package com.worldventures.dreamtrips.modules.player.playback;

import android.widget.MediaController;

public interface DtPlayer {

    void start();

    void pause();

    MediaController.MediaPlayerControl getMediaPlayerControl();
}
