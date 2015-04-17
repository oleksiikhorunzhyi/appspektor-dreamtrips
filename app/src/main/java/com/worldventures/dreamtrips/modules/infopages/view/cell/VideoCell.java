package com.worldventures.dreamtrips.modules.infopages.view.cell;

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
import com.worldventures.dreamtrips.modules.infopages.model.Video;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoFailedEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoProgressEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoStartEvent;
import com.worldventures.dreamtrips.modules.video.model.DownloadVideoEntity;

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

    public VideoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
        this.universalImageLoader.loadImage(getModelObject().getImageUrl(), this.ivBg, null);
        this.tvTitle.setText(getModelObject().getVideoName());
        DownloadVideoEntity downloadEntity = getModelObject().getDownloadEntity();
        int color = ivDownload.getContext().getResources().getColor(R.color.bucket_blue);
        circleView.setColor(color);
        ivDownload.setProgress(0);
        if (downloadEntity.isCached(context)) {
            ivDownload.setIcon(R.drawable.ic_video_done, R.drawable.ic_video_done);
            ivDownload.setProgress(100);
        } else if (downloadEntity.isFailed()) {
            ivDownload.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
            color = ivDownload.getContext().getResources().getColor(R.color.bucket_red);
            circleView.setColor(color);
        } else {
            ivDownload.setIcon(R.drawable.ic_video_download, R.drawable.ic_video_done);
        }
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        DownloadVideoEntity videoEntity = getModelObject().getDownloadEntity();
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
        DownloadVideoEntity videoEntity = getModelObject().getDownloadEntity();
        if (!videoEntity.isCached(context) && videoEntity.getProgress() == 0) {
            getEventBus().post(new DownloadVideoRequestEvent(getModelObject()));

        } else if (videoEntity.isCached(context)) {
            //TODO show delete file dialog
        } else {
            Toast.makeText(context, context.getString(R.string.download_in_progress),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void prepareForReuse() {
        ivBg.setImageResource(0);
    }


    public void onEventMainThread(DownloadVideoStartEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getMp4Url())) {
            ivDownload.setIcon(R.drawable.ic_video_download, R.drawable.ic_video_done);
            ivDownload.setProgress(0);
            int color = ivDownload.getContext().getResources().getColor(R.color.bucket_blue);
            circleView.setColor(color);
        }
    }

    public void onEventMainThread(DownloadVideoProgressEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getMp4Url())) {
            ivDownload.setProgress(event.getProgress());
        }
    }

    public void onEventMainThread(DownloadVideoFailedEvent event) {
        if (event.getEntity().getUrl().equals(getModelObject().getMp4Url())) {
            ivDownload.setProgress(0);
            ivDownload.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
            int color = ivDownload.getContext().getResources().getColor(R.color.bucket_red);
            circleView.setColor(color);
        }
    }
}
