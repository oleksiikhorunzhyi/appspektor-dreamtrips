package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.DownloadVideoEntity;

public class DownloadVideoStartEvent {
    private int id;
    private DownloadVideoEntity entity;

    public DownloadVideoStartEvent(int id, DownloadVideoEntity entity) {
        this.id = id;
        this.entity = entity;
    }

    public int getId() {
        return id;
    }

    public DownloadVideoEntity getEntity() {
        return entity;
    }
}
