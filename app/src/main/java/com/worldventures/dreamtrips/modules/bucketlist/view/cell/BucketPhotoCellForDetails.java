package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoFullscreenRequestEvent;

import butterknife.OnClick;
import butterknife.OnLongClick;

@Layout(R.layout.adapter_item_bucket_photo_cell)
public class BucketPhotoCellForDetails extends BucketPhotoCell {

    public BucketPhotoCellForDetails(View view) {
        super(view);
    }

    @OnLongClick(R.id.imageViewPhoto)
    public boolean onCellLongClick(View view) {
        showItemDialog(view);
        return true;
    }

    @OnClick(R.id.imageViewPhoto)
    public void onCellClick(View view) {
        openFullScreen();
    }

    private void openFullScreen() {
        getEventBus().post(new BucketPhotoFullscreenRequestEvent(getPosition() - 1));
    }
}
