package com.worldventures.dreamtrips.modules.player.view;

import android.widget.MediaController;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface PodcastPlayerScreen extends DtlScreen {

    void attachMediaPlayerControl(MediaController.MediaPlayerControl mediaPlayerControl);
}
