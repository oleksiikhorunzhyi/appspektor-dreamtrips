package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.feed.event.CommentsPressedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import org.w3c.dom.Text;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

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
        if (obj != null) {
            Photo photoObj = obj.getItem();
            photo.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.0f));
            loadPhoto(photoObj);
            if (!TextUtils.isEmpty(photoObj.getTitle())) {
                title.setVisibility(View.VISIBLE);
                title.setText(photoObj.getTitle());
            } else {
                title.setVisibility(View.GONE);
            }
        }
    }

    private void loadPhoto(Photo photoObj) {
        int size = itemView.getResources().getDimensionPixelSize(R.dimen.feed_item_height);
        photo.setImageURI(Uri.parse(photoObj.getImages()
                .getUrl(size, size)));
    }

    @Override
    public void prepareForReuse() {

    }
}
