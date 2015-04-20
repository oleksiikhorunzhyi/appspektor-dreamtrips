package com.worldventures.dreamtrips.modules.video.request;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

import java.io.InputStream;

import javax.inject.Inject;

public class DownloadVideoPendingListener implements PendingRequestListener<InputStream> {
    private CachedVideo cachedVideo;

    @Inject
    protected SnappyRepository db;

    public DownloadVideoPendingListener(CachedVideo cachedVideo) {
        this.cachedVideo = cachedVideo;
    }

    @Override
    public void onRequestNotFound() {
        cachedVideo.setIsFailed(true);
        db.saveDownloadVideoEntity(cachedVideo);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(InputStream inputStream) {

    }
}
