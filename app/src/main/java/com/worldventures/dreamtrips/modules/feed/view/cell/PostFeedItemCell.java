package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.bundle.EditPostBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_feed_post_event)
public class PostFeedItemCell extends FeedItemDetailsCell<PostFeedItem> {

    @InjectView(R.id.item_holder)
    View itemHolder;
    @InjectView(R.id.post)
    TextView post;

    @Optional
    @InjectView(R.id.collage)
    CollageView collageView;

    @Inject
    FragmentManager fragmentManager;
    @Inject
    Activity activity;

    public PostFeedItemCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        PostFeedItem obj = getModelObject();
        post.setText(obj.getItem().getDescription());
        //
        processAttachments(obj.getItem().getAttachments());
        if (collageView != null) {
            collageView.setItemClickListener(new CollageView.ItemClickListener() {
                @Override
                public void itemClicked(int position) {
                    List<IFullScreenObject> items = Queryable.from(getModelObject().getItem().getAttachments())
                            .filter(element -> element.getType() == FeedEntityHolder.Type.PHOTO)
                            .map(element -> (IFullScreenObject) element.getItem()).toList();
                    FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                            .position(position)
                            .userId(getModelObject().getItem().getOwner().getId())
                            .type(TripImagesType.FIXED)
                            .route(Route.SOCIAL_IMAGE_FULLSCREEN)
                            .fixedList(new ArrayList<>(items))
                            .showTags(true)
                            .build();

                    NavigationConfig config = NavigationConfigBuilder.forActivity()
                            .data(data)
                            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                            .build();

                    router.moveTo(Route.FULLSCREEN_PHOTO_LIST, config);
                }

                @Override
                public void moreClicked() {
                    router.moveTo(Route.FEED_ITEM_DETAILS, NavigationConfigBuilder.forActivity()
                            .data(new FeedDetailsBundle(getModelObject()))
                            .build());
                }
            });
        }
    }

    /**
     * If attachments exists - initialize CollageView
     *
     * @param attachments
     */
    protected void processAttachments(List<FeedEntityHolder> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            collageView.setVisibility(View.VISIBLE);
            itemHolder.post(() -> collageView.setItems(attachmentsToCollageItems(attachments), itemHolder.getWidth()));
        } else {
            collageView.setVisibility(View.GONE);
            collageView.clear();
        }
    }

    private List<CollageItem> attachmentsToCollageItems(List<FeedEntityHolder> attachments) {
        List<CollageItem> items = new ArrayList<>(attachments.size());
        Queryable.from(attachments).forEachR(itemHolder -> {
            if (itemHolder.getItem() instanceof Photo) {
                Photo photo = (Photo) itemHolder.getItem();
                items.add(new CollageItem(photo.getImages().getUrl(), photo.getWidth(), photo.getHeight()));
            }
        });
        return items;
    }

    @Override
    protected void onDelete() {
        super.onDelete();
        getEventBus().post(new DeletePostEvent(getModelObject().getItem()));
    }

    @Override
    protected void onEdit() {
        int containerId = R.id.container_details_floating;
        router.moveTo(Route.EDIT_POST, NavigationConfigBuilder.forRemoval()
                .containerId(containerId)
                .fragmentManager(fragmentManager)
                .build());
        router.moveTo(Route.EDIT_POST, NavigationConfigBuilder.forFragment()
                .containerId(containerId)
                .backStackEnabled(false)
                .fragmentManager(fragmentManager)
                .data(new EditPostBundle(getModelObject().getItem()))
                .build());
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_feed_entity_edit, R.string.post_delete, R.string.post_delete_caption);
    }
}
