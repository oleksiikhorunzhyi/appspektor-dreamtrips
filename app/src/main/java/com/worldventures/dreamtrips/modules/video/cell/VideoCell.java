package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;
import com.worldventures.dreamtrips.modules.video.model.Video;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends AbstractCell<Video> {

    @InjectView(R.id.iv_bg)
    protected ImageView ivBg;
    @InjectView(R.id.iv_play)
    protected ImageView ivPlay;
    @InjectView(R.id.tv_title)
    protected TextView tvTitle;
    @InjectView(R.id.iv_download)
    protected FabButton ivDownload;
    @InjectView(R.id.fabbutton_circle)
    protected CircleImageView circleView;


    @Inject
    protected Context context;
    @Inject
    protected UniversalImageLoader universalImageLoader;
    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    int blue;
    int red;

    public VideoCell(View view) {
        super(view);
        blue = ivDownload.getContext().getResources().getColor(R.color.bucket_blue);
        red = ivDownload.getContext().getResources().getColor(R.color.bucket_red);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
        CachedVideo downloadEntity = getModelObject().getDownloadEntity();

        this.universalImageLoader.loadImage(getModelObject().getImageUrl(), this.ivBg, null);
        this.tvTitle.setText(getModelObject().getVideoName());

        circleView.setColor(blue);
        ivDownload.setProgress(downloadEntity.getProgress());
        if (downloadEntity.isCached(context)) {
            ivDownload.setIcon(R.drawable.ic_video_done, R.drawable.ic_video_done);
        } else if (downloadEntity.isFailed()) {
            setFailedState();
        } else {
            setInProgressState();
        }
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        CachedVideo videoEntity = getModelObject().getDownloadEntity();
        Uri parse = Uri.parse(getModelObject().getMp4Url());
        if (videoEntity.isCached(context)) {
            parse = Uri.parse(videoEntity.getFilePath(context));
        }
        Intent intent = new Intent(context, PlayerActivity.class).setData(parse);
        String email = appSessionHolder.get().get().getUser().getEmail();
        TrackingHelper.playVideo(getModelObject().getVideoName(), email);
        context.startActivity(intent);
    }

    @OnClick(R.id.iv_download)
    public void onDownloadClick() {
        CachedVideo videoEntity = getModelObject().getDownloadEntity();
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

    @Override
    public void prepareForReuse() {
        ivBg.setImageResource(0);
    }

    public void onEventMainThread(DownloadVideoStartEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getMp4Url())) {
            ivDownload.setProgress(0);
            setInProgressState();
        }
    }


    public void onEventMainThread(DownloadVideoProgressEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getMp4Url())) {
            ivDownload.setProgress(event.getProgress());
        }
    }

    public void onEventMainThread(DownloadVideoFailedEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getMp4Url())) {
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
}
