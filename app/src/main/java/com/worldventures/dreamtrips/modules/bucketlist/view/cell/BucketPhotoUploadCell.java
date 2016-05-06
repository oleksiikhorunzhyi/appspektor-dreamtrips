package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoCreationItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.ActionState;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_bucket_photo_upload_cell)
public class BucketPhotoUploadCell extends AbstractDelegateCell<BucketPhotoCreationItem, BucketPhotoUploadCellDelegate> {

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

        if (getModelObject().getStatus().equals(ActionState.Status.FAIL)) {
            fabProgress.showProgress(false);
            fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
            int color = fabProgress.getContext().getResources().getColor(R.color.bucket_red);
            circleView.setColor(color);
        } else {
            fabProgress.setVisibility(View.VISIBLE);
            fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
            fabProgress.setIndeterminate(true);
            int color = fabProgress.getContext().getResources().getColor(R.color.bucket_blue);
            circleView.setColor(color);
            fabProgress.showProgress(true);
        }
    }

    @Override
    public void prepareForReuse() {
    }

    @OnClick(R.id.fab_progress)
    public void onCellClick() {
        cellDelegate.onCellClicked(getModelObject());
    }
}
