package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_photo_event)
public class FeedPhotoEventCell extends FeedHeaderCell<FeedPhotoEventModel> {

    @InjectView(R.id.photo)
    SimpleDraweeView photo;
    @InjectView(R.id.title)
    TextView title;

    public FeedPhotoEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        FeedPhotoEventModel obj = getModelObject();
        Photo photoObj = obj.getEntities()[0];
        photo.setImageURI(Uri.parse(photoObj.getImages().getUrl()));
        title.setText(photoObj.getTitle());
    }

    @Override
    public void prepareForReuse() {

    }
}
