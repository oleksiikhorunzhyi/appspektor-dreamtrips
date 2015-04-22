package com.worldventures.dreamtrips.modules.video.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.InputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class DownloadVideoListener implements PendingRequestListener<InputStream>, RequestProgressListener {
    public static final int START_VALUE = 10;
    public static final int RESIDUE = 90;
    @Inject
    @Global
    protected EventBus eventBus;
    @Inject
    protected Context context;
    @Inject
    protected SnappyRepository db;

    protected CachedEntity entity;

    protected int lastProgress = -1;

    public DownloadVideoListener(CachedEntity entity) {
        this.entity = entity;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        if (!(spiceException instanceof RequestCancelledException)) {
            Toast.makeText(context, context.getString(R.string.fail), Toast.LENGTH_SHORT).show();
            entity.setIsFailed(true);
            db.saveDownloadVideoEntity(entity);
            eventBus.post(new DownloadVideoFailedEvent(spiceException, entity));
        }

    }

    @Override
    public void onRequestSuccess(final InputStream result) {
        Log.v(this.getClass().getSimpleName(), "onRequestSuccess");
    }

    @Override
    public void onRequestProgressUpdate(RequestProgress p) {
        int progress = (int) (p.getProgress() * RESIDUE) + START_VALUE;
        if (progress > lastProgress) {

            if (progress == START_VALUE) {
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

    @Override
    public void onRequestNotFound() {
        entity.setIsFailed(true);
        db.saveDownloadVideoEntity(entity);
        eventBus.post(new DownloadVideoFailedEvent(new SpiceException("onRequestNotFound"), entity));
    }
}
