package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.infopages.model.Video;

public class DownloadVideoRequestEvent {
    private Video video;

    public DownloadVideoRequestEvent(Video modelObject) {

        this.video = modelObject;
    }

    public Video getVideo() {
        return video;
    }
}
