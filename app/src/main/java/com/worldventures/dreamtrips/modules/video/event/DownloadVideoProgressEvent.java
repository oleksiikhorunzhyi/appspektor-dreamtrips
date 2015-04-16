package com.worldventures.dreamtrips.modules.video.event;

public class DownloadVideoProgressEvent {

    private final int id;
    private final int progress;

    public DownloadVideoProgressEvent(int id, int progress) {
        this.id = id;
        this.progress = progress;
    }
}
