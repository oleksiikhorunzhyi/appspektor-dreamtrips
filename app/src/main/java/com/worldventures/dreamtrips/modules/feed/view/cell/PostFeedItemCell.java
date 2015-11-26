package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemCell;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

@Layout(R.layout.adapter_post_event)
public class PostFeedItemCell extends FeedItemCell<PostFeedItem> {

    @InjectView(R.id.post)
    TextView post;

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
    }

    @Override
    protected void onDelete() {
        super.onDelete();
        getEventBus().post(new DeletePostEvent(getModelObject().getItem()));
    }

    @Override
    protected void onEdit() {
        int containerId = R.id.container_details_floating;
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
                .containerId(containerId)
                .fragmentManager(fragmentManager)
                .build());
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
                .containerId(containerId)
                .backStackEnabled(false)
                .fragmentManager(fragmentManager)
                .data(new PostBundle(getModelObject().getItem()))
                .build());
        View container = ButterKnife.findById(activity, containerId);
        if (container != null) container.setVisibility(View.VISIBLE);
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_feed_entity_edit, R.string.post_delete, R.string.post_delete_caption);
    }
}
