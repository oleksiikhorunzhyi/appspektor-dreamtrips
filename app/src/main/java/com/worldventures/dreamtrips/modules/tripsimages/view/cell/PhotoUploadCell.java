package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import javax.inject.Inject;

import butterknife.InjectView;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;
import mbanje.kurt.fabbutton.ProgressRingView;

@Layout(R.layout.adapter_item_photo_upload)
public class PhotoUploadCell extends AbstractCell<UploadTask> {

    @InjectView(R.id.imageViewPhoto)
    protected SimpleDraweeView imageView;

    @InjectView(R.id.fab_progress)
    protected FabButton fabProgress;
    @InjectView(R.id.fabbutton_circle)
    protected CircleImageView circleView;
    @InjectView(R.id.fabbutton_ring)
    protected ProgressRingView ring;

    @Inject
    protected SnappyRepository db;

    public PhotoUploadCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        imageView.setImageURI(Uri.parse(getModelObject().getFilePath()));
        ring.setProgressColor(itemView.getResources().getColor(R.color.white));

        if (getModelObject().getStatus().equals(UploadTask.Status.FAILED)) {
            setupViewAsFailed();
        } else if (getModelObject().getStatus().equals(UploadTask.Status.COMPLETED)) {
            setupViewAsFinished();
        } else {
            setupViewAsLoading();
        }
    }

    @Override
    public void prepareForReuse() {
    }

    private void setupViewAsLoading() {
        fabProgress.setVisibility(View.VISIBLE);
        fabProgress.setIndeterminate(true);
        fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
        int color = fabProgress.getContext().getResources().getColor(R.color.bucket_blue);
        circleView.setColor(color);
        fabProgress.showProgress(true);
    }

    private void setupViewAsFailed() {
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        int color = fabProgress.getContext().getResources().getColor(R.color.bucket_red);
        circleView.setColor(color);
    }

    private void setupViewAsFinished() {
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_done, R.drawable.ic_upload_done);
        int color = fabProgress.getContext().getResources().getColor(R.color.bucket_green);
        circleView.setColor(color);
    }

}
