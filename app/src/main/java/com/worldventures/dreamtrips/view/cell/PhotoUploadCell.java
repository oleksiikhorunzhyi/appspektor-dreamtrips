package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.filippudak.ProgressPieView.ProgressPieView;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;

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

    public PhotoUploadCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        universalImageLoader.loadImage(getModelObject().getFileUri(), this.imageView, null, new SimpleImageLoadingListener());
        pb.setProgress((int) getModelObject().getProgress());
    }

    @Override
    public void prepareForReuse() {
        imageView.setImageBitmap(null);
    }

}
