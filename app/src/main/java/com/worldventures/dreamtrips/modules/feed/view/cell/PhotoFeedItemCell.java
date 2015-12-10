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
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemCell;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_photo_event)
public class PhotoFeedItemCell extends FeedItemCell<PhotoFeedItem> {

    @InjectView(R.id.photo)
    SimpleDraweeView photo;
    @InjectView(R.id.title)
    TextView title;

    @Inject
    ActivityRouter activityRouter;

    public PhotoFeedItemCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        PhotoFeedItem obj = getModelObject();
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

        photo.setOnClickListener(v -> {
            ArrayList<IFullScreenObject> items = new ArrayList<>();
            items.add(getModelObject().getItem());
            FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                    .position(0)
                    .userId(getModelObject().getItem().getOwner().getId())
                    .type(TripImagesListFragment.Type.FIXED_PHOTO_LIST)
                    .fixedList(items)
                    .build();

            NavigationBuilder.create()
                    .with(activityRouter)
                    .data(data)
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .move(Route.FULLSCREEN_PHOTO_LIST);

            getEventBus().post(new FeedItemAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW, getModelObject().getItem().getUid(),
                    FeedEntityHolder.Type.PHOTO));
        });
    }

    private void loadPhoto(Photo photoObj) {
        int size = itemView.getResources().getDimensionPixelSize(R.dimen.feed_item_height);
        photo.setController(GraphicUtils.provideFrescoResizingController(Uri.parse(photoObj.getImages()
                .getUrl(size, size)), photo.getController()));
    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_feed_entity_edit, R.string.photo_delete, R.string.photo_delete_caption);
    }

    @Override
    protected void onDelete() {
        super.onDelete();
        getEventBus().post(new DeletePhotoEvent(getModelObject().getItem()));
    }

    @Override
    protected void onEdit() {
        super.onEdit();
        NavigationBuilder.create()
                .with(activityRouter)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new EditPhotoBundle(getModelObject().getItem()))
                .attach(Route.PHOTO_EDIT);
    }

    @Override
    public void prepareForReuse() {

    }
}
