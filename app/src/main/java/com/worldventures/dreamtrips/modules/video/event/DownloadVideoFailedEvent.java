package com.worldventures.dreamtrips.modules.video.event;

import com.worldventures.dreamtrips.modules.video.model.DownloadVideoEntity;

public class DownloadVideoFailedEvent {
    private final int id;
    private final int errorCode;
    private final String errorMessage;
    private DownloadVideoEntity entity;


    public DownloadVideoFailedEvent(int id, int errorCode,
                                    String errorMessage, DownloadVideoEntity entity) {

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

    public DownloadVideoEntity getEntity() {
        return entity;
    }
}
