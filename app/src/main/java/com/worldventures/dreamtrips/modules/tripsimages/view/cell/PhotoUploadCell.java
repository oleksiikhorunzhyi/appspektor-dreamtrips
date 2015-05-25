package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.filippudak.ProgressPieView.ProgressPieView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFinished;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadStarted;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_photo_upload)
public class PhotoUploadCell extends AbstractCell<ImageUploadTask> {

    @InjectView(R.id.imageViewPhoto)
    protected SimpleDraweeView imageView;

    @InjectView(R.id.iv_result)
    protected ImageView ivResult;

    @InjectView(R.id.pb)
    protected ProgressPieView pb;

    @InjectView(R.id.vg_upload_holder)
    protected ViewGroup vgUploadHolder;

    @InjectView(R.id.btn_action)
    protected ImageButton btnCancelUpload;
    @Inject
    protected SnappyRepository db;

    public PhotoUploadCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }

        imageView.setImageURI(Uri.parse(getModelObject().getFileUri()));

        if (getModelObject().isFailed()) {
            setupViewAsFailed();
        }
    }

    @Override
    public void prepareForReuse() {
        Log.d("Progress event", "prepareForReuse");
        imageView.setImageBitmap(null);
        pb.setProgress(0);
        pb.setVisibility(View.VISIBLE);
        ivResult.setBackgroundResource(R.drawable.circle_blue);
        btnCancelUpload.setImageResource(R.drawable.ic_upload_cloud);
    }

    @OnClick(R.id.btn_action)
    public void onBtnAction() {
        //TODO
    }

    public void onEventMainThread(PhotoUploadStarted event) {
        if (getModelObject().getTaskId().equalsIgnoreCase(event.getUploadTask().getTaskId())) {
            pb.setProgress(0);
            pb.setVisibility(View.VISIBLE);
            ivResult.setBackgroundResource(R.drawable.circle_blue);
            btnCancelUpload.setImageResource(R.drawable.ic_upload_cloud);
            TrackingHelper.photoUploadStarted(getModelObject().getType(), "");
        }
    }

    public void onEventMainThread(UploadProgressUpdateEvent event) {
        if (getModelObject().getTaskId().equalsIgnoreCase(event.getTaskId())) {
            Log.d("Progress event", event.getProgress() + "");
            if (event.getProgress() <= 100) {
                if (event.getProgress() > pb.getProgress())
                    pb.setProgress(event.getProgress());
                Log.i("Progress event", "set progress:" + event.getProgress());

            }
        }
    }

    public void onEventMainThread(PhotoUploadFailedEvent event) {
        if (event.getTaskId().equals(getModelObject().getTaskId())) {
            new Handler().postDelayed(() -> {
                Log.i("Progress event", "PhotoUploadFailed");
                  setupViewAsFailed();
            }, 300);
        }
    }

    private void setupViewAsFailed() {
        pb.setProgress(0);
        pb.setVisibility(View.INVISIBLE);
        ivResult.setBackgroundResource(R.drawable.circle_red);
        btnCancelUpload.setImageResource(R.drawable.ic_upload_retry);
    }


    public void onEventMainThread(PhotoUploadFinished event) {
        Log.i("Progress event", "PhotoUploadFinished");

        if (event.getPhoto().getTaskId().equals(getModelObject().getTaskId())) {
            pb.setVisibility(View.INVISIBLE);
            ivResult.setBackgroundResource(R.drawable.circle_green);
            btnCancelUpload.setImageResource(R.drawable.ic_upload_done);
            TrackingHelper.photoUploadFinished(getModelObject().getType(), "");
        }
    }

}
