package com.worldventures.dreamtrips.modules.video.api;

import android.content.Context;
import android.widget.Toast;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.FileCachingDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.InputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class DownloadFileListener implements PendingRequestListener<InputStream>, RequestProgressListener {

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
    protected FileCachingDelegate fileCachingDelegate;

    protected int lastProgress = -1;

    public DownloadFileListener(CachedEntity entity, FileCachingDelegate fileCachingDelegate) {
        this.entity = entity;
        this.fileCachingDelegate = fileCachingDelegate;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Timber.v("onRequestFailure");
        if (!(spiceException instanceof RequestCancelledException)) {
            Toast.makeText(context, context.getString(R.string.fail), Toast.LENGTH_SHORT).show();
            entity.setIsFailed(true);
            db.saveDownloadMediaEntity(entity);
            if (fileCachingDelegate != null) fileCachingDelegate.updateItem(entity);
        }
    }

    @Override
    public void onRequestSuccess(final InputStream result) {
        Timber.v("onRequestSuccess");
    }

    @Override
    public void onRequestProgressUpdate(RequestProgress p) {
        Timber.v("onRequestProgressUpdate");
        int progress = (int) (p.getProgress() * RESIDUE) + START_VALUE;
        if (progress > lastProgress) {
            if (progress == START_VALUE) {
                entity.setIsFailed(false);
            }
            lastProgress = progress;
            entity.setProgress(progress);
            db.saveDownloadMediaEntity(entity);
            if (fileCachingDelegate != null) fileCachingDelegate.updateItem(entity);
        }
    }

    @Override
    public void onRequestNotFound() {
        Timber.v("onRequestNotFound");
        if (fileCachingDelegate != null) fileCachingDelegate.downloadFile(entity);
    }
}
