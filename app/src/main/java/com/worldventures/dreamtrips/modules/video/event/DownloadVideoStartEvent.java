package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class DownloadVideoStartEvent {
    private int id;
    private CachedEntity entity;

    public DownloadVideoStartEvent(CachedEntity entity) {
        this.id = id;
        this.entity = entity;
    }

    public int getId() {
        return id;
    }

    public CachedEntity getEntity() {
        return entity;
    }
}
