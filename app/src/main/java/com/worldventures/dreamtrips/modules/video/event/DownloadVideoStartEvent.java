package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class DownloadVideoStartEvent {
    private int id;
    private CachedVideo entity;

    public DownloadVideoStartEvent(int id, CachedVideo entity) {
        this.id = id;
        this.entity = entity;
    }

    public int getId() {
        return id;
    }

    public CachedVideo getEntity() {
        return entity;
    }
}
