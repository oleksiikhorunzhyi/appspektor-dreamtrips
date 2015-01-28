package com.worldventures.dreamtrips.view.cell;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.filippudak.ProgressPieView.ProgressPieView;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.busevents.CancelUpload;
import com.worldventures.dreamtrips.utils.busevents.UploadProgressUpdateEvent;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

@Layout(R.layout.adapter_item_photo_upload)
public class PhotoUploadCell extends AbstractCell<ImageUploadTask> {

    @InjectView(R.id.iv_bg)
    public ImageView imageView;

    @InjectView(R.id.pb)
    public ProgressPieView pb;

    @InjectView(R.id.vg_upload_holder)
    ViewGroup vgUploadHolder;

    @Inject
    UniversalImageLoader universalImageLoader;

    @Inject
    @Global
    EventBus eventBus;

    public PhotoUploadCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        universalImageLoader.loadImage(getModelObject().getFileUri(), this.imageView, null, new SimpleImageLoadingListener());
    }

    @Override
    public void prepareForReuse() {
        imageView.setImageBitmap(null);
        pb.setProgress(0);
    }

    @Override
    public void afterInject() {
        super.afterInject();
        eventBus.register(this);
    }

    @OnClick(R.id.cancel_upload)
    public void onCancelUpload() {
        eventBus.post(new CancelUpload(getModelObject()));
    }

    public void onEventMainThread(UploadProgressUpdateEvent event) {
        if (getModelObject().getTaskId().equalsIgnoreCase(event.getTaskId())) {
            pb.setProgress(event.getProgress());
        }
    }
}
