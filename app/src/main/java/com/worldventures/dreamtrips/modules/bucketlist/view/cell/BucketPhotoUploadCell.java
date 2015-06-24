package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoReuploadRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
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
        ivPhoto.setImageURI(Uri.parse(getModelObject().getFilePath()));

        if (getModelObject().getProgress() == 100) {
            fabProgress.setVisibility(View.GONE);
        } else {
            fabProgress.setVisibility(View.VISIBLE);
            fabProgress.setProgress(getModelObject().getProgress());
        }
        if (getModelObject().isFailed()) {
            fabProgress.setProgress(0);
            fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
            int color = fabProgress.getContext().getResources().getColor(R.color.bucket_red);
            circleView.setColor(color);
        }
    }

    @Override
    public void prepareForReuse() {
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
}
