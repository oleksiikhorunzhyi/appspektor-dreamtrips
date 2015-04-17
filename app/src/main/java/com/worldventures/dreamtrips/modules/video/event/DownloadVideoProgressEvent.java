package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.DownloadVideoEntity;

public class DownloadVideoProgressEvent {

    private final int id;
    private final int progress;
    private DownloadVideoEntity entity;

    public DownloadVideoProgressEvent(int id, int progress, DownloadVideoEntity entity) {
        this.id = id;
        this.progress = progress;
        this.entity = entity;
    }

    public int getId() {
        return id;
    }

    public int getProgress() {
        return progress;
    }

    public DownloadVideoEntity getEntity() {
        return entity;
    }
}
