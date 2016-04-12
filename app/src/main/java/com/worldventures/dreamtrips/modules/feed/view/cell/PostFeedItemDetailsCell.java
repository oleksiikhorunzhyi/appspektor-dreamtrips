package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.bundle.EditPostBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_post_event)
public class PostFeedItemDetailsCell extends FeedItemDetailsCell<PostFeedItem> {

    @InjectView(R.id.item_holder)
    View itemHolder;
    @InjectView(R.id.post)
    TextView post;
    @InjectView(R.id.collage)
    CollageView collageView;

    @Inject
    FragmentManager fragmentManager;
    @Inject
    Activity activity;

    public PostFeedItemDetailsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        PostFeedItem obj = getModelObject();
        post.setText(obj.getItem().getDescription());
        //
        processCollageCase(obj.getItem().getAttachments());
    }

    /**
     * If attachments exists - initialize CollageView
     *
     * @param attachments
     */
    private void processCollageCase(List<FeedEntityHolder> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            collageView.setVisibility(View.VISIBLE);
            collageView.setItems(attachmentsToCollageItems(attachments), itemHolder.getWidth());
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
