package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Annotations.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends AbstractCell<Video> {

    @InjectView(R.id.iv_bg)
    protected SimpleDraweeView ivBg;
    @InjectView(R.id.iv_play)
    protected ImageView ivPlay;
    @InjectView(R.id.tv_title)
    protected TextView tvTitle;
    @InjectView(R.id.iv_download)
    protected FabButton ivDownload;
    @InjectView(R.id.fabbutton_circle)
    protected CircleImageView circleView;

    @Inject
    @ForActivity
    protected Context context;
    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    protected ProgressVideoCellHelper progressVideoCellHelper;

    public VideoCell(View view) {
        super(view);
        progressVideoCellHelper = new ProgressVideoCellHelper(ivDownload, circleView);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(progressVideoCellHelper)) {
            getEventBus().register(progressVideoCellHelper);
        }

        ivBg.setImageURI(Uri.parse(getModelObject().getImageUrl()));
        tvTitle.setText(getModelObject().getVideoName());

        progressVideoCellHelper.setModelObject(getModelObject().getCacheEntity());
        progressVideoCellHelper.setUrl(getModelObject().getMp4Url());

        progressVideoCellHelper.syncUIStateWithModel();
    }

    @Override
    public void clearResources() {
        super.clearResources();
        EventBus eventBus = getEventBus();
        if (eventBus != null && eventBus.isRegistered(progressVideoCellHelper)) {
            eventBus.unregister(progressVideoCellHelper);
        }
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        CachedEntity videoEntity = getModelObject().getCacheEntity();
        Uri parse = Uri.parse(getModelObject().getMp4Url());
        if (videoEntity.isCached(context)) {
            parse = Uri.parse(CachedEntity.getFilePath(context,videoEntity.getUrl()));
        }
        Intent intent = new Intent(context, PlayerActivity.class).setData(parse);
        String email = appSessionHolder.get().get().getUser().getEmail();
        TrackingHelper.playVideo(getModelObject().getVideoName(), email);
        context.startActivity(intent);
    }

    @OnClick(R.id.iv_download)
    public void onDownloadClick() {
        progressVideoCellHelper.onDownloadCLick(context,getEventBus());
    }

    @Override
    public void prepareForReuse() {
        ivBg.setImageResource(0);
    }

}
