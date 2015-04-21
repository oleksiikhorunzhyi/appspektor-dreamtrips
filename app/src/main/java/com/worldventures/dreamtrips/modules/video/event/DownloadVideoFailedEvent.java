package com.worldventures.dreamtrips.modules.video.event;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class DownloadVideoFailedEvent {

    private SpiceException spiceException;
    private CachedEntity entity;

    public DownloadVideoFailedEvent(SpiceException spiceException, CachedEntity entity) {
        this.spiceException = spiceException;

        this.entity = entity;
    }

    public String getId() {
        return entity.getUuid();
    }

    public CachedEntity getEntity() {
        return entity;
    }

    public SpiceException getSpiceException() {
        return spiceException;
    }
}
