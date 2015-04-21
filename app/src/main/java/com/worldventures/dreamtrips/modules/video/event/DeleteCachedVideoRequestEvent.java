package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class DeleteCachedVideoRequestEvent {
    private CachedVideo videoEntity;

    public DeleteCachedVideoRequestEvent(CachedVideo videoEntity) {
        this.videoEntity = videoEntity;
    }

    public CachedVideo getVideoEntity() {
        return videoEntity;
    }
}
