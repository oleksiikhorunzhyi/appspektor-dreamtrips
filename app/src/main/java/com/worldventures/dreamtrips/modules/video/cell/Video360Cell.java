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
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
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

    private ProgressVideoCellHelper progressVideoCellHelper;

    public Video360Cell(View view) {
        super(view);
        progressVideoCellHelper = new ProgressVideoCellHelper(ivDownload, circleView);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(progressVideoCellHelper)) {
            getEventBus().register(progressVideoCellHelper);
        }
        this.universalImageLoader.loadImage(getModelObject().getThumbnail(), this.imageViewPreview, null);
        this.textViewTitle.setText(getModelObject().getTitle());
        this.textViewDuration.setText(getModelObject().getDuration());
        this.ivDownload.setProgress(getModelObject().getCacheEntity().getProgress());

        progressVideoCellHelper.setModelObject(getModelObject().getCacheEntity());
        progressVideoCellHelper.setUrl(getModelObject().getURL());
        progressVideoCellHelper.syncUIStateWithModel();
    }

    @OnClick(R.id.iv_bg)
    public void onItemClick() {
        CachedVideo cacheEntity = getModelObject().getCacheEntity();
        String url = getModelObject().getURL();
        if (cacheEntity.isCached(context)) {
            url = cacheEntity.getUrl();
        }
        activityRouter.open360Activity(url);
    }


    @OnClick(R.id.iv_download)
    public void onDownloadClick() {
      progressVideoCellHelper.onDownloadCLick(context,getEventBus());
    }

    @Override
    public void prepareForReuse() {
        imageViewPreview.setImageResource(0);
    }
}
