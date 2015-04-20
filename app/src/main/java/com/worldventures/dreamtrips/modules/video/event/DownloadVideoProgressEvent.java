package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class DownloadVideoProgressEvent {

    private final int progress;
    private CachedVideo entity;

    public DownloadVideoProgressEvent(int progress, CachedVideo entity) {
        this.progress = progress;
        this.entity = entity;
    }

    public int getProgress() {
        return progress;
    }

    public CachedVideo getEntity() {
        return entity;
    }
}
