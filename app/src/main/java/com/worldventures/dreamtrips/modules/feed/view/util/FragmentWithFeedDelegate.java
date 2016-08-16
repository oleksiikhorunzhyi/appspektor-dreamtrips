package com.worldventures.dreamtrips.modules.feed.view.util;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.UndefinedFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.model.ReloadFeedModel;
import com.worldventures.dreamtrips.modules.profile.view.cell.ReloadFeedCell;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;

import java.util.List;

public class FragmentWithFeedDelegate {

    Router router;

    private BaseDelegateAdapter adapter;

    public FragmentWithFeedDelegate(Router router) {
        this.router = router;
    }

    public void init(BaseDelegateAdapter adapter) {
        this.adapter = adapter;
        registerBaseFeedCells();
    }

    public void registerAdditionalCell(Class<?> itemClass, Class<? extends AbstractCell> cellClass) {
        adapter.registerCell(itemClass, cellClass);
    }

    public void registerDelegate(Class<?> itemClass, CellDelegate<?> cellDelegate) {
        adapter.registerDelegate(itemClass, cellDelegate);
    }

    public void addItem(Object item) {
        adapter.addItem(item);
    }

    public void addItem(int position, Object item) {
        adapter.addItem(position, item);
    }

    public void addItems(List items) {
        adapter.addItems(items);
    }

    public void updateItem(Object item) {
        adapter.updateItem(item);
    }

    public Object getItem(int position) {
        return adapter.getItem(position);
    }

    public List getItems() {
        return adapter.getItems();
    }

    public int getItemsCount() {
        return adapter.getCount();
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void notifyItemInserted(int position) {
        adapter.notifyItemInserted(position);
    }

    public void notifyItemChanged(FeedItem feedItem) {
        if (feedItem == null) {
            adapter.notifyDataSetChanged();
            return;
        }
        int size = adapter.getItems().size();
        for (int i = 0; i < size; i++) {
            Object object = adapter.getItems().get(i);
            if (object instanceof FeedItem && ((FeedItem)object).equalsWith(feedItem)) {
                adapter.notifyItemChanged(i);
                return;
            }
        }
    }

    /**
     * After leaving feed items list screen all translated items should reset view state to original
     * (translation hides and 'Translation' button is visible)
     */
    public void resetTranslatedStatus() {
        Queryable.from(adapter.getItems()).forEachR(item -> {
            if (item instanceof FeedItem) ((FeedItem)item).setTranslated(false);
        });
        notifyDataSetChanged();
    }

    public void clearItems() {
        adapter.clear();
    }

    private void registerBaseFeedCells() {
        adapter.registerCell(ReloadFeedModel.class, ReloadFeedCell.class);
        adapter.registerCell(PhotoFeedItem.class, FeedItemCell.class);
        adapter.registerCell(TripFeedItem.class, FeedItemCell.class);
        adapter.registerCell(BucketFeedItem.class, FeedItemCell.class);
        adapter.registerCell(PostFeedItem.class, FeedItemCell.class);
        adapter.registerCell(UndefinedFeedItem.class, UndefinedFeedItemDetailsCell.class);
        adapter.registerCell(LoadMoreModel.class, LoaderCell.class);
    }

    /////////////////////////////////////////
    //  Routes
    ////////////////////////////////////////

    public void openAccountProfile(User account) {
        router.moveTo(Route.ACCOUNT_PROFILE, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(account))
                .build());
    }

    public void openFriends(FriendMainBundle bundle) {
        router.moveTo(Route.FRIENDS, NavigationConfigBuilder.forActivity()
                .data(bundle)
                .build());
    }

    public void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle) {
        router.moveTo(route, NavigationConfigBuilder.forActivity()
                .data(foreignBucketBundle)
                .build());
    }

    public void openBucketEdit(FragmentManager fragmentManager, boolean isTabletLandscape,
                               BucketBundle bucketBundle) {
        @IdRes int containerId = R.id.container_details_floating;
        bucketBundle.setLock(true);
        if (isTabletLandscape) {
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forFragment()
                    .backStackEnabled(true)
                    .containerId(containerId)
                    .fragmentManager(fragmentManager)
                    .data(bucketBundle)
                    .build());
        } else {
            router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forActivity()
                    .data(bucketBundle)
                    .build());
        }
    }

    public void openTripImages(Route route, TripsImagesBundle tripImagesBundle) {
        router.moveTo(route, NavigationConfigBuilder.forActivity()
                .data(tripImagesBundle)
                .build());
    }

    public void openComments(FeedItem feedItem, boolean isVisible, boolean isTabletLandscape) {
        if (isVisible) {
            Route detailsRoute = Route.FEED_ITEM_DETAILS;
            FeedItemDetailsBundle.Builder bundleBuilder = new FeedItemDetailsBundle.Builder()
                    .feedItem(feedItem)
                    .showAdditionalInfo(true)
                    .openKeyboard(true);
            if (isTabletLandscape) {
                bundleBuilder.slave(true);
            }
            router.moveTo(detailsRoute, NavigationConfigBuilder.forActivity()
                    .data(bundleBuilder.build())
                    .build());
        }
    }

    public void openFriendsSearch() {
        router.moveTo(Route.FRIEND_SEARCH, NavigationConfigBuilder.forActivity()
                .data(new FriendGlobalSearchBundle(""))
                .build());
    }

    public void openPost(FragmentManager fragmentManager) {
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
                .containerId(R.id.container_details_floating)
                .fragmentManager(fragmentManager)
                .build());
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(fragmentManager)
                .containerId(R.id.container_details_floating)
                .build());
    }

    public void openSharePhoto(FragmentManager fragmentManager, CreateEntityBundle bundle) {
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
                .containerId(R.id.container_details_floating)
                .fragmentManager(fragmentManager)
                .build());
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(fragmentManager)
                .containerId(R.id.container_details_floating)
                .data(bundle)
                .build());
    }

    public void openFeedAdditionalInfo(FragmentManager fragmentManager, User account) {
        router.moveTo(Route.FEED_LIST_ADDITIONAL_INFO, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(fragmentManager)
                .containerId(R.id.additional_info_container)
                .data(new FeedAdditionalInfoBundle(account))
                .build());
    }

    public void openHashtagSearch() {
        router.moveTo(Route.FEED_HASHTAG, NavigationConfigBuilder.forActivity()
                .data(null)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(true).build())
                .build());
    }
}
