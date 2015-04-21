package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class DeleteCachedVideoRequestEvent {
    private CachedEntity videoEntity;

    public DeleteCachedVideoRequestEvent(CachedEntity videoEntity) {
        this.videoEntity = videoEntity;
    }

    public CachedEntity getVideoEntity() {
        return videoEntity;
    }
}
