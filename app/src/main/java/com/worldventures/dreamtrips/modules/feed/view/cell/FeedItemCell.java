package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.util.CommentCellHelper;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.adapter_feed_item_cell)
public class FeedItemCell<ITEM extends FeedItem> extends AbstractCell<ITEM> {

    @InjectView(R.id.cell_container)
    ViewGroup cellContainer;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    CommentCellHelper commentCellHelper;

    private FeedItemDetailsCell feedItemDetailsCell;

    public FeedItemCell(View view) {
        super(view);
    }

    @Override
    public void fillWithItem(ITEM item) {
        if (feedItemDetailsCell == null) {
            feedItemDetailsCell = createCell(item);
            cellContainer.addView(feedItemDetailsCell.itemView);
            feedItemDetailsCell.setEventBus(getEventBus());
            injectorProvider.get().inject(feedItemDetailsCell);
            feedItemDetailsCell.afterInject();
            //
            commentCellHelper = new CommentCellHelper(itemView.getContext());
            commentCellHelper.attachView(itemView);
            feedItemDetailsCell.itemView.setOnClickListener(view -> feedItemDetailsCell.openItemDetails());
        }
        //
        feedItemDetailsCell.fillWithItem(item);
        super.fillWithItem(item);
    }

    private FeedItemDetailsCell createCell(ITEM item) {
        LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
        switch (item.getType()) {
            case POST:
                return new PostFeedItemDetailsCell(inflater.inflate(R.layout.adapter_post_event, null));
            case PHOTO:
                return new PhotoFeedItemDetailsCell(inflater.inflate(R.layout.adapter_item_feed_photo_event, null));
            case TRIP:
                return new TripFeedItemDetailsCell(inflater.inflate(R.layout.adapter_item_feed_trip_event, null));
            case BUCKET_LIST_ITEM:
                return new BucketFeedItemDetailsCell(inflater.inflate(R.layout.adapter_item_feed_bucket_event, null));
            default:
                return new UndefinedFeedItemDetailsCell(inflater.inflate(R.layout.adapter_item_feed_undefined_event, null));
        }
    }

    @Override
    protected void syncUIStateWithModel() {
        if (commentCellHelper != null) {
            List<Comment> commentList = getModelObject().getItem().getComments();
            Comment comment = commentList.isEmpty() ? null : Queryable.from(commentList).lastOrDefault();
            if (comment != null) {
                commentCellHelper.showContainer();
                commentCellHelper.set(comment, injectorProvider.get());
            } else {
                commentCellHelper.hideContainer();
            }
        }
        /**
         * temporary, while we don't redesign feed likers
         */
        hideLikersPanel();
    }

    private void hideLikersPanel() {
        View likersPanel = itemView.findViewById(R.id.likers_panel);
        if (likersPanel != null) likersPanel.setVisibility(View.GONE);
    }

    @Override
    public void prepareForReuse() {

    }

    @Optional
    @OnClick(R.id.comment_preview)
    void commentsPreviewClicked() {
        feedItemDetailsCell.openItemDetails();
    }

    @Optional
    @OnClick(R.id.user_photo)
    void commentOwnerClicked() {
        User user = commentCellHelper.getComment().getOwner();
        router.moveTo(routeCreator.createRoute(user.getId()), NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(user))
                .build());
    }
}
