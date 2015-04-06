package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.adapter_item_bucket_photo_cell)
public class BucketAddPhotoCell extends AbstractCell<Object> {

    @InjectView(R.id.iv_photo)
    ImageView ivPhoto;

    public BucketAddPhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        ivPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivPhoto.setImageResource(R.drawable.ic_bucket_add_pic);
        ivPhoto.setBackgroundColor(ivPhoto.getContext().getResources().getColor(R.color.bucket_add_phto));
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.iv_photo)
    public void onClick() {
        getEventBus().post(new BucketAddPhotoClickEvent());
    }
}