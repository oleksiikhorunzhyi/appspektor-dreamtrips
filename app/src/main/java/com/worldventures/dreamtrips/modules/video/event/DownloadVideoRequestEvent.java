package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class DownloadVideoRequestEvent {

    private CachedVideo video;

    public DownloadVideoRequestEvent(CachedVideo video) {
        this.video = video;
    }

    public CachedVideo getCachedVideo() {
        return video;
    }
}
