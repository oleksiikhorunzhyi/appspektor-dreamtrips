package com.worldventures.dreamtrips.modules.video.request;

import android.net.Uri;

import com.techery.spares.module.Annotations.Global;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class DownloadVideoRequest extends DownloadRequest {
    @Inject
    @Global
    EventBus eventBus;

    public DownloadVideoRequest(Uri uri) {
        super(uri);
        setPriority(DownloadRequest.Priority.HIGH);
        setDownloadListener(new DownloadStatusListener() {

            @Override
            public void onDownloadComplete(int id) {
                eventBus.post(new DownloadVideoStartEvent(id));
            }

            @Override
            public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                eventBus.post(new DownloadVideoFailedEvent(id, errorCode, errorMessage));

            }

            @Override
            public void onProgress(int id, long totalBytes, int progress) {
                eventBus.post(new DownloadVideoProgressEvent(id,progress));
            }
        });
    }
}
