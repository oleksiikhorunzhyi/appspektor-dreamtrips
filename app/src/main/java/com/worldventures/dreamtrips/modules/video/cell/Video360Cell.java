package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;
import com.worldventures.dreamtrips.modules.video.model.Video360;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_video_360)
public class Video360Cell extends AbstractCell<Video360> {

    @InjectView(R.id.textViewDuration)
    protected TextView textViewDuration;

    @InjectView(R.id.tv_title)
    protected TextView textViewTitle;

    @InjectView(R.id.iv_bg)
    protected ImageView imageViewPreview;

    @InjectView(R.id.iv_download)
    protected FabButton ivDownload;

    @InjectView(R.id.fabbutton_circle)
    protected CircleImageView circleView;

    @Inject
    protected UniversalImageLoader universalImageLoader;

    @Inject
    protected ActivityRouter activityRouter;

    @Inject
    protected Context context;

    int blue;
    int red;

    public Video360Cell(View view) {
        super(view);
        blue = ivDownload.getContext().getResources().getColor(R.color.bucket_blue);
        red = ivDownload.getContext().getResources().getColor(R.color.bucket_red);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
        this.universalImageLoader.loadImage(getModelObject().getThumbnail(), this.imageViewPreview, null);
        this.textViewTitle.setText(getModelObject().getTitle());
        this.textViewDuration.setText(getModelObject().getDuration());
        this.ivDownload.setProgress(getModelObject().getCacheEntity().getProgress());

        CachedVideo downloadEntity = getModelObject().getCacheEntity();

        ivDownload.setProgress(downloadEntity.getProgress());
        if (downloadEntity.isFailed()) {
            setFailedState();
        } else {
            setInProgressState();
        }
    }

    @OnClick(R.id.iv_bg)
    void onItemClick() {
        CachedVideo cacheEntity = getModelObject().getCacheEntity();
        String url = getModelObject().getURL();
        if (cacheEntity.isCached(context)) {
            url = cacheEntity.getUrl();
        }
        activityRouter.open360Activity(url);
    }


    @OnClick(R.id.iv_download)
    public void onDownloadClick() {
        CachedVideo videoEntity = getModelObject().getCacheEntity();
        if ((!videoEntity.isCached(context) && videoEntity.getProgress() == 0)
                || videoEntity.isFailed()) {
            getEventBus().post(new DownloadVideoRequestEvent(videoEntity));
        } else if (videoEntity.isCached(context)) {
            getEventBus().post(new DeleteCachedVideoRequestEvent(videoEntity));
        } else {
            String message = context.getString(R.string.download_in_progress);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onEventMainThread(DownloadVideoStartEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getURL())) {
            ivDownload.setProgress(0);
            setInProgressState();
        }
    }

    public void onEventMainThread(DownloadVideoProgressEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getURL())) {
            ivDownload.setProgress(event.getProgress());
        }
    }

    public void onEventMainThread(DownloadVideoFailedEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getURL())) {
            setFailedState();
        }
    }

    private void setFailedState() {
        ivDownload.setProgress(0);
        ivDownload.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        circleView.setColor(red);
    }

    private void setInProgressState() {
        ivDownload.setIcon(R.drawable.ic_video_download, R.drawable.ic_video_done);
        circleView.setColor(blue);
    }


    @Override
    public void prepareForReuse() {
        imageViewPreview.setImageResource(0);
    }
}
