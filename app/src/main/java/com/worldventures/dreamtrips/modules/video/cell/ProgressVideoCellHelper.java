package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;

import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.video.event.CancelCachingVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import de.greenrobot.event.EventBus;

public class ProgressVideoCellHelper {

    private final PinProgressButton pinProgressButton;

    private String url;
    private CachedEntity cacheEntity;

    public ProgressVideoCellHelper(PinProgressButton pinProgressButton) {
        this.pinProgressButton = pinProgressButton;
    }

    public void onEventMainThread(DownloadVideoStartEvent event) {
        if (event.getEntity().getUrl().equals(url)) {
            pinProgressButton.setProgress(0);
            setInProgressState();
        }
    }

    public void onEventMainThread(DownloadVideoFailedEvent event) {
        if (event.getEntity().getUrl().equals(url)) {
            setFailedState();
        }
    }

    public void syncUIStateWithModel() {
        pinProgressButton.setProgress(cacheEntity.getProgress());
        if (cacheEntity.isFailed()) {
            setFailedState();
        } else {
            setInProgressState();
        }
    }

    private void setFailedState() {
        pinProgressButton.setFailed(true);
    }

    private void setInProgressState() {
        pinProgressButton.setFailed(false);
        if (cacheEntity.isCached(pinProgressButton.getContext())) {
            pinProgressButton.setProgress(100);
        } else {
            pinProgressButton.setProgress(cacheEntity.getProgress());
        }
    }

    public void setModelObject(CachedEntity cacheEntity) {
        this.cacheEntity = cacheEntity;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void onDownloadCLick(Context context, EventBus eventBus) {
        boolean cached = cacheEntity.isCached(context);
        boolean inProgress = cacheEntity.getProgress() > 0 && cacheEntity.getProgress() < 100;
        boolean failed = cacheEntity.isFailed();
        if ((!cached && !inProgress) || failed) {
            eventBus.post(new DownloadVideoRequestEvent(cacheEntity));
        } else if (cached) {
            eventBus.post(new DeleteCachedVideoRequestEvent(cacheEntity));
        } else {
            eventBus.post(new CancelCachingVideoRequestEvent(cacheEntity));
        }
    }
}
