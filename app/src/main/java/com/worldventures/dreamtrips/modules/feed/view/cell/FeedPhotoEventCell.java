package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.Collections;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_photo_event)
public class FeedPhotoEventCell extends FeedHeaderCell<FeedPhotoEventModel> {

    @InjectView(R.id.photo)
    SimpleDraweeView photo;
    @InjectView(R.id.title)
    TextView title;
    @Inject
    ActivityRouter router;

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
        itemView.setOnClickListener(view -> {
            String url = getModelObject().getEntities()[0].getImages().getUrl();
            TripImage tripImage = new TripImage();
            tripImage.setUrl(url);
            router.openFullScreenTrip(Collections.singletonList((Object) tripImage), 0);
        });

    }

    @Override
    public void prepareForReuse() {

    }
}
