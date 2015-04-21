package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class DownloadVideoRequestEvent {

    private CachedEntity video;

    public DownloadVideoRequestEvent(CachedEntity video) {
        this.video = video;
    }

    public CachedEntity getCachedVideo() {
        return video;
    }
}
