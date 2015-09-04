package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_photo_event)
public class FeedPhotoEventCell extends FeedHeaderCell<FeedPhotoEventModel> {

    @InjectView(R.id.photo)
    SimpleDraweeView photo;
    @InjectView(R.id.title)
    TextView title;

    @Inject
    ActivityRouter activityRouter;

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

        itemView.setOnClickListener(v -> {
            ArrayList<Photo> items = new ArrayList<>();
            items.add(getModelObject().getItem());
            Bundle args = new Bundle();
            args.putSerializable(FullScreenPhotoWrapperFragment.EXTRA_POSITION, 0);
            args.putSerializable(FullScreenPhotoWrapperFragment.EXTRA_TYPE, TripImagesListFragment.Type.FIXED_LIST);
            args.putSerializable(FullScreenPhotoWrapperFragment.EXTRA_FIXED_LIST, items);
            NavigationBuilder.create()
                    .with(activityRouter)
                    .args(args)
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .move(Route.FULLSCREEN_PHOTO_LIST);
        });
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