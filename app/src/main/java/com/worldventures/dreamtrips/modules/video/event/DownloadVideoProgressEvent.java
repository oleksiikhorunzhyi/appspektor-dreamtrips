package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class DownloadVideoProgressEvent {

    private final int progress;
    private CachedEntity entity;

    public DownloadVideoProgressEvent(int progress, CachedEntity entity) {
        this.progress = progress;
        this.entity = entity;
    }

    public int getProgress() {
        return progress;
    }

    public CachedEntity getEntity() {
        return entity;
    }
}
