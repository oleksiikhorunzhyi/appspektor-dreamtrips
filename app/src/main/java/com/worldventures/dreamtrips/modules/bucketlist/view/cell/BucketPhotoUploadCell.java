package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.net.Uri;
import android.view.View;

import com.apptentive.android.sdk.Log;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoReuploadRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFailedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadStarted;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_bucket_photo_upload_cell)
public class BucketPhotoUploadCell extends AbstractCell<BucketPhotoUploadTask> {

    @InjectView(R.id.imageViewPhoto)
    protected SimpleDraweeView ivPhoto;
    @InjectView(R.id.fab_progress)
    protected FabButton fabProgress;
    @InjectView(R.id.fabbutton_circle)
    protected CircleImageView circleView;

    public BucketPhotoUploadCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
        ivPhoto.setImageURI(Uri.parse(getModelObject().getFilePath()));
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
            fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
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
        if (event.getProgress() == 100) {
            fabProgress.setVisibility(View.GONE);
        } else {
            fabProgress.setVisibility(View.VISIBLE);
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
