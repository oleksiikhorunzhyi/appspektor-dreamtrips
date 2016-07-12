package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.os.Environment;

import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.membership.view.cell.delegate.PodcastCellDelegate;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class ProgressVideoCellHelper {

    private final PinProgressButton pinProgressButton;

    private CachedEntity cacheEntity;

    public ProgressVideoCellHelper(PinProgressButton pinProgressButton) {
        this.pinProgressButton = pinProgressButton;
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

    public void onDownloadClick(Context context, VideoCellDelegate delegate) {
        if (delegate == null) return;
        //
        boolean cached = cacheEntity.isCached(context);
        boolean inProgress = cacheEntity.getProgress() > 0 && cacheEntity.getProgress() < 100;
        boolean failed = cacheEntity.isFailed();
        if ((!cached && !inProgress) || failed) {
            delegate.onDownloadVideo(cacheEntity);
        } else if (cached) {
            delegate.onDeleteVideo(cacheEntity);
        } else {
            delegate.onCancelCachingVideo(cacheEntity);
        }
    }

    public void onDownloadClick(PodcastCellDelegate delegate) {
        if (delegate == null) return;
        //
        boolean cached = cacheEntity.isCached(Environment.DIRECTORY_PODCASTS);
        boolean inProgress = cacheEntity.getProgress() > 0 && cacheEntity.getProgress() < 100;
        boolean failed = cacheEntity.isFailed();
        if ((!cached && !inProgress) || failed) {
            delegate.onDownloadPodcast(cacheEntity);
        } else if (cached) {
            delegate.onDeletePodcast(cacheEntity);
        } else {
            delegate.onCancelCachingPodcast(cacheEntity);
        }
    }
}
