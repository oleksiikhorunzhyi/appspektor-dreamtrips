package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_feed_post_details)
public class PostFeedItemDetailsCell extends PostFeedItemCell {

    @InjectView(R.id.item_holder)
    View itemHolder;

    @Optional
    @InjectView(R.id.imagesList)
    RecyclerView imagesList;
    @Inject
    @ForActivity
    Injector injector;
    private BaseDelegateAdapter adapter;

    public PostFeedItemDetailsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        LinearLayoutManager layout = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        layout.setAutoMeasureEnabled(true);
        imagesList.setLayoutManager(layout);
        adapter = new BaseDelegateAdapter<>(itemHolder.getContext(), injector);
        adapter.registerCell(Photo.class, SubPhotoAttachmentCell.class);
        imagesList.setAdapter(adapter);
        super.syncUIStateWithModel();
    }

    protected void processAttachments(List<FeedEntityHolder> attachments) {
        Queryable.from(attachments).forEachR(itemHolder -> {
            if (itemHolder.getItem() instanceof Photo) {
                adapter.addItem(itemHolder.getItem());
            }
        });
    }

    @Override
    protected void onDelete() {
        super.onDelete();
        getEventBus().post(new DeletePostEvent(getModelObject().getItem()));
    }


    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_feed_entity_edit, R.string.post_delete, R.string.post_delete_caption);
    }
}
