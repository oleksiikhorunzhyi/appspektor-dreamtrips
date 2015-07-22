package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.TrackVideoStatusEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends AbstractCell<Video> {

    @InjectView(R.id.iv_bg)
    protected SimpleDraweeView ivBg;
    @InjectView(R.id.iv_play)
    protected ImageView ivPlay;
    @InjectView(R.id.tv_title)
    protected TextView tvTitle;

    @InjectView(R.id.download_progress)
    protected PinProgressButton downloadProgress;

    @Inject
    @ForActivity
    protected Context context;

    protected ProgressVideoCellHelper progressVideoCellHelper;

    public VideoCell(View view) {
        super(view);
        progressVideoCellHelper = new ProgressVideoCellHelper(downloadProgress);
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
            parse = Uri.parse(CachedEntity.getFilePath(context, videoEntity.getUrl()));
        }
        Intent intent = new Intent(context, PlayerActivity.class).setData(parse);
        getEventBus().post(new TrackVideoStatusEvent(TrackingHelper.ACTION_MEMBERSHIP_PLAY,
                getModelObject().getVideoName()));
        context.startActivity(intent);
    }

    @OnClick(R.id.download_progress)
    public void onDownloadClick() {
        progressVideoCellHelper.onDownloadCLick(context, getEventBus());
        getEventBus().post(new TrackVideoStatusEvent(TrackingHelper.ACTION_MEMBERSHIP_LOAD_START,
                getModelObject().getVideoName()));
    }

    @Override
    public void prepareForReuse() {
        ivBg.setImageResource(0);
    }

}
