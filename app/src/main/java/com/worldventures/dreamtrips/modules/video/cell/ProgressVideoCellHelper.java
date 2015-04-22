package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.widget.Toast;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.video.event.CancelCachingVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import de.greenrobot.event.EventBus;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

public class ProgressVideoCellHelper {

    private final FabButton ivDownload;
    private final CircleImageView circleView;

    private int blue;
    private int red;
    private String url;
    private CachedEntity cacheEntity;

    ProgressVideoCellHelper(FabButton ivDownload, CircleImageView circleView) {

        this.ivDownload = ivDownload;
        this.circleView = circleView;

        blue = ivDownload.getContext().getResources().getColor(R.color.bucket_blue);
        red = ivDownload.getContext().getResources().getColor(R.color.bucket_red);
    }

    public void onEventMainThread(DownloadVideoStartEvent event) {
        if (event.getEntity().getUrl().equals(url)) {
            ivDownload.setProgress(0);
            setInProgressState();
        }
    }

    public void onEventMainThread(DownloadVideoProgressEvent event) {
        if (event.getEntity().getUrl().equals(url)) {
            ivDownload.setProgress(event.getProgress());
        }
    }

    public void onEventMainThread(DownloadVideoFailedEvent event) {
        if (event.getEntity().getUrl().equals(url)) {
            setFailedState();
        }
    }

    public void syncUIStateWithModel() {
        ivDownload.setProgress(cacheEntity.getProgress());
        if (cacheEntity.isFailed()) {
            setFailedState();
        } else {
            setInProgressState();
        }
        if (cacheEntity.getProgress() == 0) {
            ivDownload.setIcon(R.drawable.ic_video_download, R.drawable.ic_video_download);
        }

    }

    private void setFailedState() {
        ivDownload.setProgress(0);
        ivDownload.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        circleView.setColor(red);
    }

    private void setInProgressState() {
        circleView.setColor(blue);
        ivDownload.setIcon(R.drawable.ic_video_download, R.drawable.ic_video_done);
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
