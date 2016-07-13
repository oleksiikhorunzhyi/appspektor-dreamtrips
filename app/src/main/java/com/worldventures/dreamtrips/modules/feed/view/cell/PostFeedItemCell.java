package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.custom.HashtagTextView;
import com.worldventures.dreamtrips.modules.feed.bundle.EditPostBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedHashtagBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_feed_post_event)
public class PostFeedItemCell extends FeedItemDetailsCell<PostFeedItem> {

    @InjectView(R.id.post)
    HashtagTextView post;
    @InjectView(R.id.card_view_wrapper)
    View cardViewWrapper;
    @Optional
    @InjectView(R.id.collage)
    CollageView collageView;
    @Optional
    @InjectView(R.id.tag)
    ImageView tag;

    @Inject
    FragmentManager fragmentManager;
    @Inject
    Activity activity;

    private int width;

    public PostFeedItemCell(View view) {
        super(view);
        itemView.post(() -> width = cardViewWrapper.getWidth());
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        PostFeedItem obj = getModelObject();
        if (width > 0) {
            itemView.setVisibility(View.VISIBLE);
            processAttachments(obj.getItem().getAttachments());
            processPostText(obj.getItem());
        } else {
            itemView.setVisibility(View.INVISIBLE);
            itemView.post(this::syncUIStateWithModel);
        }
    }

    private void processPostText(TextualPost textualPost) {
        if (!TextUtils.isEmpty(textualPost.getDescription())) {
            post.setVisibility(View.VISIBLE);
            post.setText(String.format("%s", textualPost.getDescription()));
            post.setHashtagClickListener(this::openHashtagFeeds);

            List<String> clickableHashtags = Queryable
                    .from(textualPost.getHashtags())
                    .map(element -> element != null ? element.getName() : null)
                    .toList();

            List<String> hightlightedHashtags = getModelObject().getMetaData() != null && getModelObject().getMetaData().getHashtags() != null ?
                    Queryable.from(getModelObject()
                            .getMetaData()
                            .getHashtags())
                            .map(element -> element != null ? element.getName() : null)
                            .toList()
                    : new ArrayList<>();

            post.highlightHashtags(clickableHashtags, hightlightedHashtags);
        } else {
            post.setVisibility(View.GONE);
        }
    }

    protected void processAttachments(List<FeedEntityHolder> attachments) {
        if (collageView == null) return;
        //
        if (attachments != null && !attachments.isEmpty()) {
            collageView.setItemClickListener(new CollageView.ItemClickListener() {
                @Override
                public void itemClicked(int position) {
                    openFullscreenPhotoList(position);
                }

                @Override
                public void moreClicked() {
                    openFeedItemDetails();
                }
            });
            collageView.setItems(attachmentsToCollageItems(attachments), width);
        } else {
            collageView.clear();
        }
        processTags(attachments);
    }

    private void processTags(List<FeedEntityHolder> attachments) {
        if (tag != null) tag.setVisibility(isHasTags(attachments) ? View.VISIBLE : View.GONE);
    }

    private boolean isHasTags(List<FeedEntityHolder> attachments) {
        return Queryable.from(attachments)
                .count(attachment -> attachment.getType() == FeedEntityHolder.Type.PHOTO &&
                        ((Photo) attachment.getItem()).getPhotoTagsCount() > 0) > 0;
    }

    private void openFullscreenPhotoList(int position) {
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

    private void openFeedItemDetails() {
        router.moveTo(Route.FEED_ITEM_DETAILS, NavigationConfigBuilder.forActivity()
                .data(new FeedDetailsBundle(getModelObject()))
                .build());
    }

    private List<CollageItem> attachmentsToCollageItems(List<FeedEntityHolder> attachments) {
        return Queryable.from(attachments)
                .map(element -> (Photo) element.getItem())
                .map(photo -> {
                    return new CollageItem(photo.getImages().getUrl(), photo.getWidth(), photo.getHeight());
                })
                .toList();
    }

    private void openHashtagFeeds(@NotNull String hashtag) {
        router.moveTo(Route.FEED_HASHTAG, NavigationConfigBuilder.forActivity()
                .data(new FeedHashtagBundle(hashtag))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(true).build())
                .build());
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
