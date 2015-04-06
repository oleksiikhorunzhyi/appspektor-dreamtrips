package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_bucket_photo_cell)
public class BucketPhotoCell extends AbstractCell<BucketPhoto> {

    @InjectView(R.id.iv_photo)
    ImageView ivPhoto;

    @Inject
    protected UniversalImageLoader imageLoader;

    public BucketPhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        imageLoader.loadImage(getModelObject().getUrl(), ivPhoto, UniversalImageLoader.OP_DEF);
    }

    @Override
    public void prepareForReuse() {

    }
}
