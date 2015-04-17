package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

public class DownloadVideoFailedEvent {
    private final int id;
    private final int errorCode;
    private final String errorMessage;
    private CachedVideo entity;


    public DownloadVideoFailedEvent(int id, int errorCode,
                                    String errorMessage, CachedVideo entity) {

        this.id = id;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.entity = entity;
    }

    public int getId() {
        return id;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public CachedVideo getEntity() {
        return entity;
    }
}
