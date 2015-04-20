package com.worldventures.dreamtrips.modules.video.request;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

import java.io.InputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class DownloadVideoListener implements RequestListener<InputStream>, RequestProgressListener {
    @Inject
    @Global
    protected EventBus eventBus;
    @Inject
    protected Context context;
    @Inject
    protected SnappyRepository db;

    CachedVideo entity;

    protected int lastProgress = -1;

    public DownloadVideoListener(CachedVideo entity) {
        this.entity = entity;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(context, context.getString(R.string.fail), Toast.LENGTH_SHORT).show();

        eventBus.post(new DownloadVideoFailedEvent(spiceException, entity));

        entity.setIsFailed(true);
        db.saveDownloadVideoEntity(entity);
    }

    @Override
    public void onRequestSuccess(final InputStream result) {
        Log.v(this.getClass().getSimpleName(), "onRequestSuccess");
    }

    @Override
    public void onRequestProgressUpdate(RequestProgress p) {
        int progress = (int) (p.getProgress() * 100);
        if (progress > lastProgress) {

            if (progress == 0) {
                entity.setIsFailed(false);
                db.saveDownloadVideoEntity(entity);
                eventBus.post(new DownloadVideoStartEvent(entity));
            }
            lastProgress = progress;
            entity.setProgress(progress);
            db.saveDownloadVideoEntity(entity);
            eventBus.post(new DownloadVideoProgressEvent(progress, entity));
        }
    }
}
