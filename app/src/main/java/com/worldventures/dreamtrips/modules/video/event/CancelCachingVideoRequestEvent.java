package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class CancelCachingVideoRequestEvent {
    private CachedEntity cacheEntity;

    public CancelCachingVideoRequestEvent(CachedEntity cacheEntity) {

        this.cacheEntity = cacheEntity;
    }

    public CachedEntity getCacheEntity() {
        return cacheEntity;
    }
}
