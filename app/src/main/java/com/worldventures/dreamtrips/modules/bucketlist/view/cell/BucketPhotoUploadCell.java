package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.view.View;
import android.widget.ImageView;

import com.apptentive.android.sdk.Log;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoReuploadRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFailedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadStarted;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_bucket_photo_upload_cell)
public class BucketPhotoUploadCell extends AbstractCell<BucketPhotoUploadTask> {

    @InjectView(R.id.iv_photo)
    protected ImageView ivPhoto;
    @InjectView(R.id.fab_progress)
    protected FabButton fabProgress;
    @InjectView(R.id.fabbutton_circle)
    protected CircleImageView circleView;

    @Inject
    protected UniversalImageLoader imageLoader;

    public BucketPhotoUploadCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
        imageLoader.loadImage(getModelObject().getFilePath(), ivPhoto, UniversalImageLoader.OP_DEF);
    }

    @Override
    public void prepareForReuse() {
        Log.i(this.getClass().getSimpleName(), "prepareForReuse");
    }

    @OnClick(R.id.fab_progress)
    public void onCellClick() {
        if (getModelObject().isFailed()) {
            getEventBus().post(new BucketPhotoReuploadRequestEvent(getModelObject()));
            getModelObject().setFailed(false);
            fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_done);
            fabProgress.setProgress(0);
            int color = fabProgress.getContext().getResources().getColor(R.color.bucket_blue);
            circleView.setColor(color);
        } else {
            getEventBus().post(new BucketPhotoUploadCancelRequestEvent(getModelObject()));
        }
    }

    public void onEventMainThread(BucketPhotoUploadStarted event) {
        if (getModelObject().getTaskId() == event.getBucketPhoto().getTaskId()) {
            fabProgress.setVisibility(View.VISIBLE);
        }
    }


    public void onEventMainThread(UploadProgressUpdateEvent event) {
        String bucketId = String.valueOf(getModelObject().getTaskId());
        if (bucketId.equals(event.getTaskId())) {
            fabProgress.setProgress(event.getProgress());
        }
    }

    public void onEventMainThread(BucketPhotoUploadFailedEvent event) {
        if (getModelObject().getTaskId() == event.getTaskId()) {
            fabProgress.setProgress(0);
            fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
            int color = fabProgress.getContext().getResources().getColor(R.color.bucket_red);
            circleView.setColor(color);
            getModelObject().setFailed(true);
        }
    }
}