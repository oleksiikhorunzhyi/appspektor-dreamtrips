package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemCell;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_post_event)
public class PostFeedItemCell extends FeedItemCell<PostFeedItem> {

    @InjectView(R.id.post)
    TextView post;

    @Inject
    FragmentCompass fragmentCompass;

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
        fragmentCompass.removePost();
        fragmentCompass.setContainerId(R.id.container_details_floating);
        fragmentCompass.disableBackStack();
        fragmentCompass.showContainer();
        //
        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(new PostBundle(getModelObject().getItem()))
                .attach(Route.POST_CREATE);
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_feed_entity_edit, R.string.post_delete, R.string.post_delete_caption);
    }
}
