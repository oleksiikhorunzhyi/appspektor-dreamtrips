package com.worldventures.dreamtrips.modules.video.event;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class DownloadVideoFailedEvent {

    private SpiceException spiceException;
    private CachedVideo entity;

    public DownloadVideoFailedEvent(SpiceException spiceException, CachedVideo entity) {

        this.spiceException = spiceException;
        this.entity = entity;
    }

    public int getId() {
        return Integer.parseInt(entity.getUuid());
    }

    public CachedVideo getEntity() {
        return entity;
    }
}
