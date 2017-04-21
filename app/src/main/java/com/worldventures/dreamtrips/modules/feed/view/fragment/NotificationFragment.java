package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.HeaderItem;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeImageView;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.NotificationHeaderAdapter;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_notification)
@MenuResource(R.menu.menu_notifications)
public class NotificationFragment extends RxBaseFragment<NotificationPresenter> implements NotificationPresenter.View, SwipeRefreshLayout.OnRefreshListener {

   @InjectView(R.id.ll_empty_view) ViewGroup emptyView;

   private BadgeImageView friendsBadge;

   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private Bundle savedInstanceState;
   private NotificationAdapter adapter;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
   }

   @Override
   public void onResume() {
      super.onResume();
      TrackingHelper.viewNotificationsScreen();
      getPresenter().reload();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter = new NotificationAdapter(getActivity(), this);
      adapter.setHasStableIds(true);
      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
      statePaginatedRecyclerViewManager.init(adapter, savedInstanceState);
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.setPaginationListener(this::loadNext);
      statePaginatedRecyclerViewManager.stateRecyclerView.setEmptyView(emptyView);
      //
      NotificationHeaderAdapter headerAdapter = new NotificationHeaderAdapter(adapter.getItems(),
            R.layout.adapter_item_notification_divider, item -> () -> createHeaderString(item));
      StickyHeadersItemDecoration decoration = new StickyHeadersBuilder().setAdapter(adapter)
            .setStickyHeadersAdapter(headerAdapter, false)
            .setOnHeaderClickListener((header, headerId) -> {})// make sticky header clickable to make items below it not clickable
            .setRecyclerView(statePaginatedRecyclerViewManager.stateRecyclerView)
            .build();
      statePaginatedRecyclerViewManager.addItemDecoration(decoration);
      statePaginatedRecyclerViewManager.stateRecyclerView.setEmptyView(emptyView);
      //
      registerCells();
   }

   private void loadNext() {
      if (!statePaginatedRecyclerViewManager.isNoMoreElements() &&
            Queryable.from(adapter.getItems()).firstOrDefault(item -> item instanceof LoadMoreModel) == null) {
         adapter.addItem(new LoadMoreModel());
         adapter.notifyDataSetChanged();
         getPresenter().loadNext();
      }
   }

   private String createHeaderString(Object item) {
      if (item instanceof FeedItem) {
         return getString(((FeedItem) item).getReadAt() == null ? R.string.notifaction_new : R.string.notifaction_older);
      } else return null;
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      friendsBadge = (BadgeImageView) MenuItemCompat.getActionView(menu.findItem(R.id.action_friend_requests));
      friendsBadge.setOnClickListener(v -> router.moveTo(Route.FRIENDS, NavigationConfigBuilder.forActivity()
            .data(new FriendMainBundle(FriendMainBundle.REQUESTS))
            .build()));
      getPresenter().refreshRequestsCount();
   }

   @Override
   protected NotificationPresenter createPresenter(Bundle savedInstanceState) {
      return new NotificationPresenter();
   }

   @Override
   public void startLoading() {
      statePaginatedRecyclerViewManager.startLoading();
   }

   @Override
   public void finishLoading() {
      statePaginatedRecyclerViewManager.finishLoading();
   }

   @Override
   public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
      statePaginatedRecyclerViewManager.updateLoadingStatus(loading, noMoreElements);
   }

   @Override
   public void refreshNotifications(List<FeedItem> notifications) {
      adapter.clearAndUpdateItems(notifications);
   }

   @Override
   public void onRefresh() {
      getPresenter().reload();
   }

   @Override
   public void setRequestsCount(int count) {
      if (friendsBadge != null) {
         friendsBadge.setBadgeValue(count);
      }
   }

   public static class NotificationAdapter extends BaseArrayListAdapter {

      private static final long LOADER_ID = Long.MIN_VALUE;

      public NotificationAdapter(Context context, Injector injector) {
         super(context, injector);
      }

      @Override
      public long getItemId(int position) {
         long id = super.getItemId(position);
         return id != RecyclerView.NO_ID ? id : LOADER_ID;
      }
   }

   private void registerCells() {
      this.adapter.registerCell(PhotoFeedItem.class, NotificationCell.class);
      this.adapter.registerCell(TripFeedItem.class, NotificationCell.class);
      this.adapter.registerCell(BucketFeedItem.class, NotificationCell.class);
      this.adapter.registerCell(PostFeedItem.class, NotificationCell.class);
      this.adapter.registerCell(UndefinedFeedItem.class, NotificationCell.class);
      this.adapter.registerCell(LoadMoreModel.class, LoaderCell.class);
   }
}
