package com.worldventures.dreamtrips.modules.video.event;

public class DownloadVideoFailedEvent {
    private final int id;
    private final int errorCode;
    private final String errorMessage;

    public DownloadVideoFailedEvent(int id, int errorCode, String errorMessage) {

        this.id = id;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
