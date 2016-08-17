package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_feed_post_details)
public class PostFeedItemDetailsCell extends PostFeedItemCell {

    @InjectView(R.id.item_holder) View itemHolder;
    @Optional @InjectView(R.id.imagesList) RecyclerView imagesList;

    @Inject @ForActivity Injector injector;

    private BaseDelegateAdapter adapter;
    private LinearLayoutManager layout;

    public PostFeedItemDetailsCell(View view) {
        super(view);
    }

    @Override
    public void afterInject() {
        super.afterInject();
        adapter = new BaseDelegateAdapter<>(itemHolder.getContext(), injector);
        adapter.registerCell(Photo.class, SubPhotoAttachmentCell.class);
        adapter.registerDelegate(Photo.class, new CellDelegate<Photo>() {
            @Override
            public void onCellClicked(Photo model) {
                openFullsreenPhoto(model);
            }
        });
        layout = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        layout.setAutoMeasureEnabled(true);
    }

    @Override
    protected void syncUIStateWithModel() {
        imagesList.setLayoutManager(layout);
        if (adapter != imagesList.getAdapter()) imagesList.setAdapter(adapter);

        super.syncUIStateWithModel();
    }

    protected void openFullsreenPhoto(Photo model) {
        List<IFullScreenObject> items = Queryable.from(getModelObject().getItem().getAttachments())
                .filter(element -> element.getType() == FeedEntityHolder.Type.PHOTO)
                .map(element -> (IFullScreenObject) element.getItem()).toList();
        FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                .position(getPositionOfPhoto(model))
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

    protected int getPositionOfPhoto(Photo model) {
        int result = 0;
        List<FeedEntityHolder> attachments = getModelObject().getItem().getAttachments();
        for (int i = 0; i < attachments.size(); i++) {
            FeedEntityHolder feedEntityHolder = attachments.get(i);
            if (feedEntityHolder.getItem().equals(model)) {
                result = i;
            }
        }
        return result;
    }

    protected void processAttachments(List<FeedEntityHolder> attachments) {
        adapter.clear();
        Queryable.from(attachments).forEachR(itemHolder -> {
            if (itemHolder.getItem() instanceof Photo) {
                adapter.addItem(itemHolder.getItem());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDelete() {
        super.onDelete();
        getEventBus().post(new DeletePostEvent(getModelObject().getItem()));
    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_feed_entity_edit, R.string.post_delete, R.string.post_delete_caption);
    }
}
