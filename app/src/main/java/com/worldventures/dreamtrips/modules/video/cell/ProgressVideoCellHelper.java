package com.worldventures.dreamtrips.modules.video.cell;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

public class ProgressVideoCellHelper {

    private final FabButton ivDownload;
    private final CircleImageView circleView;

    private int blue;
    private int red;
    private String url;
    private CachedVideo cacheEntity;

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
        if (cacheEntity.isFailed()) {
            setFailedState();
        } else {
            setInProgressState();
        }
        ivDownload.setProgress(cacheEntity.getProgress());
        if (cacheEntity.getProgress() < 100) {
            ivDownload.invalidate();
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

    public void setModelObject(CachedVideo cacheEntity) {
        this.cacheEntity = cacheEntity;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
