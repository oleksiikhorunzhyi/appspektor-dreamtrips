package com.worldventures.dreamtrips.modules.video.cell;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.events.TrackVideoStatusEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video_360)
public class Video360Cell extends AbstractCell<Video> {

    @InjectView(R.id.textViewDuration)
    protected TextView textViewDuration;

    @InjectView(R.id.tv_title)
    protected TextView textViewTitle;

    @InjectView(R.id.iv_bg)
    protected SimpleDraweeView imageViewPreview;

    @InjectView(R.id.download_progress)
    protected PinProgressButton downloadProgress;

    @Inject
    protected ActivityRouter activityRouter;

    @Inject
    protected Context context;

    private ProgressVideoCellHelper progressVideoCellHelper;

    public Video360Cell(View view) {
        super(view);
        progressVideoCellHelper = new ProgressVideoCellHelper(downloadProgress);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(progressVideoCellHelper)) {
            getEventBus().register(progressVideoCellHelper);
        }

        imageViewPreview.setImageURI(Uri.parse(getModelObject().getImageUrl()));
        this.textViewTitle.setText(getModelObject().getVideoName());
        this.textViewDuration.setText(getModelObject().getDuration());

        progressVideoCellHelper.setModelObject(getModelObject().getCacheEntity());
        progressVideoCellHelper.setUrl(getModelObject().getMp4Url());
        progressVideoCellHelper.syncUIStateWithModel();
    }

    @Override
    public void clearResources() {
        super.clearResources();
        getEventBus().unregister(progressVideoCellHelper);
    }

    @OnClick(R.id.iv_bg)
    public void onItemClick() {
        Video video = getModelObject();
        CachedEntity cacheEntity = getModelObject().getCacheEntity();
        String url = getModelObject().getMp4Url();
        if (cacheEntity.isCached(context)) {
            url = CachedEntity.getFilePath(context, getModelObject().getMp4Url());
        }
        activityRouter.open360Activity(url);
        getEventBus().post(new TrackVideoStatusEvent(TrackingHelper.ACTION_360_PLAY, video.getVideoName()));
        TrackingHelper.actionTripVideo(TrackingHelper.ATTRIBUTE_VIEW, video.getUid());
    }


    @OnClick(R.id.download_progress)
    public void onDownloadClick() {
        Video video = getModelObject();
        progressVideoCellHelper.onDownloadCLick(context, getEventBus());
        getEventBus().post(new TrackVideoStatusEvent(TrackingHelper.ACTION_360_LOAD_START, video.getVideoName()));
        TrackingHelper.actionTripVideo(TrackingHelper.ATTRIBUTE_DOWNLOAD, video.getUid());
    }

    @Override
    public void prepareForReuse() {
        imageViewPreview.setImageResource(0);
    }
}
