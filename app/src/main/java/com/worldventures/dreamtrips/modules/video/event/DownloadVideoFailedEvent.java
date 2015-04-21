package com.worldventures.dreamtrips.modules.video.event;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class DownloadVideoFailedEvent {

    private CachedVideo entity;

    public DownloadVideoFailedEvent(SpiceException spiceException, CachedVideo entity) {

        this.entity = entity;
    }

    public String getId() {
        return entity.getUuid();
    }

    public CachedVideo getEntity() {
        return entity;
    }
}
