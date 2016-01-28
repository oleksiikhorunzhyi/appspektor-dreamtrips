package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_photo_event)
public class PhotoFeedItemDetailsCell extends FeedItemDetailsCell<PhotoFeedItem> {

    @InjectView(R.id.photo)
    SimpleDraweeView photo;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.tag)
    protected ImageView tag;
    
    @Inject
    Router router;

    public PhotoFeedItemDetailsCell(View view) {
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
            tag.setVisibility(photoObj.getPhotoTagsCount() > 0 ? View.VISIBLE : View.GONE);
        }

        photo.setOnClickListener(v -> {
            ArrayList<IFullScreenObject> items = new ArrayList<>();
            items.add(getModelObject().getItem());
            FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                    .position(0)
                    .userId(getModelObject().getItem().getOwner().getId())
                    .type(TripImagesType.FIXED)
                    .route(Route.SOCIAL_IMAGE_FULLSCREEN)
                    .fixedList(items)
                    .build();

            router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity()
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .data(data)
                    .build());

            getEventBus().post(new FeedItemAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW, getModelObject().getItem().getUid(),
                    FeedEntityHolder.Type.PHOTO));
        });
    }

    private void loadPhoto(Photo photoObj) {
        int size = itemView.getResources().getDimensionPixelSize(R.dimen.feed_item_height);
        photo.setController(GraphicUtils.provideFrescoResizingController(Uri.parse(photoObj.getImages()
                .getUrl(size, size)), photo.getController()));
    }

    @OnClick(R.id.tag)
    public void onTagClick(View view){
        ArrayList<IFullScreenObject> items = new ArrayList<>();
        items.add(getModelObject().getItem());
        FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                .position(0)
                .userId(getModelObject().getItem().getOwner().getId())
                .type(TripImagesType.FIXED)
                .route(Route.SOCIAL_IMAGE_FULLSCREEN)
                .fixedList(items)
                .showTags(true)
                .build();

        NavigationConfig config = NavigationConfigBuilder.forActivity()
                .data(data)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .build();
        router.moveTo(Route.FULLSCREEN_PHOTO_LIST, config);
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
        router.moveTo(Route.PHOTO_EDIT, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new EditPhotoBundle(getModelObject().getItem()))
                .build());
    }

    @Override
    public void prepareForReuse() {

    }
}