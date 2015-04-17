package com.worldventures.dreamtrips.modules.video.request;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class DownloadVideoRequest extends DownloadRequest {
    @Inject
    @Global
    EventBus eventBus;

    @Inject
    Context context;
    @Inject
    SnappyRepository db;
    int lastProgress;

    public DownloadVideoRequest(CachedVideo entity, Injector injector) {
        super(Uri.parse(entity.getUrl()));
        injector.inject(this);
        setDestinationURI(Uri.parse(entity.getFilePath(context)));
        setPriority(DownloadRequest.Priority.HIGH);
        setDownloadListener(new DownloadStatusListener() {

            @Override
            public void onDownloadComplete(int id) {

            }

            @Override
            public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                eventBus.post(new DownloadVideoFailedEvent(id, errorCode, errorMessage, entity));
                Log.w("DownloadVideoRequest", "onDownloadFailed: " + errorMessage);
                entity.setIsFailed(true);
                db.saveDownloadVideoEntity(entity);

            }

            @Override
            public void onProgress(int id, long totalBytes, int progress) {
                if (progress == 0) {
                    eventBus.post(new DownloadVideoStartEvent(id, entity));
                    entity.setIsFailed(false);
                    db.saveDownloadVideoEntity(entity);

                }
                if (progress > lastProgress) {
                    Log.w("DownloadVideoRequest", "progress: " + progress);
                    lastProgress = progress;
                    entity.setProgress(progress);
                    db.saveDownloadVideoEntity(entity);
                    eventBus.post(new DownloadVideoProgressEvent(id, progress, entity));
                }
            }
        });
    }

}
