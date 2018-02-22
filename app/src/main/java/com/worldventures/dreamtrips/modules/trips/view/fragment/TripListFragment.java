package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.fragment.FragmentUtil;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.config.model.TravelBannerRequirement;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripListPresenter;
import com.worldventures.dreamtrips.modules.trips.view.cell.BannerCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.BannerCellDelegate;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCellDelegate;
import com.worldventures.dreamtrips.social.ui.activity.SocialMainActivity;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.util.SweetDialogHelper;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityDetailsFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;
import rx.Observable;

@Layout(R.layout.fragment_trip_list)
@MenuResource(R.menu.menu_dream_trips)
public class TripListFragment extends RxBaseFragment<TripListPresenter> implements TripListPresenter.View,
      SwipeRefreshLayout.OnRefreshListener, TripCellDelegate {

   @InjectView(R.id.recyclerViewTrips) protected EmptyRecyclerView recyclerView;
   @InjectView(R.id.ll_empty_view) protected ViewGroup emptyView;
   @InjectView(R.id.empty_search_travel_banner) protected View emptySearchTravelBanner;
   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;

   private BaseDelegateAdapter<Object> adapter;

   private SearchView searchView;
   RecyclerViewStateDelegate stateDelegate;

   private WeakHandler weakHandler;
   private GridLayoutManager layoutManager;
   private BannerCell emptySearchBannerCell;

   @State boolean searchOpened;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      weakHandler = new WeakHandler();
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      stateDelegate.saveStateIfNeeded(outState);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      stateDelegate.setRecyclerView(recyclerView);
      layoutManager = new GridLayoutManager(getActivity(), getSpanCount());
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setEmptyView(emptyView);

      BannerCellDelegate delegate = new BannerCellDelegate() {
         @Override
         public void onCellClicked(TravelBannerRequirement model) {
            Intent intent = IntentUtils.browserIntent(model.getUrl());
            FragmentUtil.startSafely(TripListFragment.this, intent);
         }

         @Override
         public void onCancelClicked() {
            getPresenter().hideTripRequirement();
         }
      };

      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(TripModel.class, TripCell.class);
      adapter.registerCell(TravelBannerRequirement.class, BannerCell.class);
      adapter.registerDelegate(TripModel.class, this);
      adapter.registerDelegate(TravelBannerRequirement.class, delegate);
      emptySearchBannerCell = new BannerCell(emptySearchTravelBanner);
      emptySearchBannerCell.setCellDelegate(delegate);

      recyclerView.setAdapter(adapter);

      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

      showFilters();

      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int itemCount = recyclerView.getLayoutManager().getItemCount();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition == itemCount - 1) {
               getPresenter().scrolled();
            }
         }
      });
   }

   private void showFilters() {
      Fragment filtersFragment = getFragmentManager().findFragmentById(R.id.container_filters);
      if (filtersFragment != null && filtersFragment.getClass().getName().equals(FiltersFragment.class.getName())) {
         return;
      }

      router.moveTo(FiltersFragment.class, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .containerId(R.id.container_filters)
            .fragmentManager(getFragmentManager())
            .build());
   }

   @Override
   protected void restoreState(Bundle savedInstanceState) {
      super.restoreState(savedInstanceState);
   }

   private int getSpanCount() {
      int spanCount;
      if (ViewUtils.isLandscapeOrientation(getActivity())) {
         spanCount = ViewUtils.isTablet(getActivity()) ? 3 : 2;
      } else {
         spanCount = ViewUtils.isTablet(getActivity()) ? 2 : 1;
      }
      return spanCount;
   }

   @Override
   public void dataSetChanged() {
      adapter.notifyDataSetChanged();
   }

   @Override
   public void showErrorMessage() {
      ((SocialMainActivity) getActivity()).informUser(getString(R.string.smth_went_wrong));
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      MenuItem searchItem = menu.findItem(R.id.action_search);
      if (searchItem != null) {
         if (searchOpened) {
            getView().post(() -> searchItem.expandActionView());
         }
         MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
               searchOpened = true;
               return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
               searchOpened = false;
               return true;
            }
         });
         searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
         searchView.setQueryHint(getString(R.string.search_trips));
         searchView.setQuery(getPresenter().getQuery(), false);
         getPresenter().onMenuInflated();
      }
   }

   @Override
   public Observable<String> textChanges() {
      return RxSearchView.queryTextChanges(searchView)
            .skip(1)
            .map(CharSequence::toString)
            .throttleLast(600, TimeUnit.MILLISECONDS)
            .debounce(200, TimeUnit.MILLISECONDS)
            .onBackpressureLatest();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_filter:
            ((SocialMainActivity) getActivity()).openRightDrawer();
            break;
         case R.id.action_map:
            getPresenter().openMap();
            break;
         default:
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @OnClick(R.id.textViewResetFilters)
   public void resetFilters() {
      getPresenter().resetFilters();
      clearSearch();
   }

   @Override
   public void onDestroyView() {
      stateDelegate.onDestroyView();
      this.recyclerView.setAdapter(null);
      super.onDestroyView();
   }

   @Override
   public void onRefresh() {
      getPresenter().reload();
   }

   @Override
   protected TripListPresenter createPresenter(Bundle savedInstanceState) {
      return new TripListPresenter();
   }

   @Override
   public void startLoading() {
      weakHandler.post(() -> {
         if (refreshLayout != null) {
            refreshLayout.setRefreshing(true);
         }
      });
   }

   @Override
   public void finishLoading() {
      weakHandler.post(() -> {
         if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
         }
      });
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   public void itemsChanged(List<Object> items) {
      layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
         @Override
         public int getSpanSize(int position) {
            if (adapter.getItem(position) instanceof TravelBannerRequirement) {
               return getSpanCount();
            }
            return 1;
         }
      });

      if (items.size() == 1 && items.get(0) instanceof TravelBannerRequirement) {
         adapter.clear();
         emptySearchBannerCell.fillWithItem((TravelBannerRequirement) items.get(0));
         emptySearchTravelBanner.setVisibility(View.VISIBLE);
      } else {
         adapter.clearAndUpdateItems(items);
         emptySearchTravelBanner.setVisibility(View.GONE);
      }
   }

   @Override
   public void itemLiked(FeedEntity feedEntity) {
      TripModel trip = Queryable.from(adapter.getItems())
            .filter(element -> element instanceof TripModel)
            .cast(TripModel.class)
            .firstOrDefault(element -> element.getUid().equals(feedEntity.getUid()));

      if (trip != null) {
         trip.syncLikeState(feedEntity);
         dataSetChanged();
         if (isVisibleOnScreen()) {
            new SweetDialogHelper().notifyTripLiked(getActivity(), trip.getName(), trip.isLiked());
         }
      }
   }

   @Override
   public void showItemAddedToBucketList(BucketItem bucketItem) {
      new SweetDialogHelper().notifyItemAddedToBucket(getActivity(), bucketItem);
   }

   @Override
   public boolean isSearchOpened() {
      return searchOpened;
   }

   public void clearSearch() {
      if (searchView != null) {
         searchView.setQuery("", true);
         searchView.clearFocus();
      }
   }

   @Override
   public void openMap() {
      router.moveTo(TripMapFragment.class, NavigationConfigBuilder.forFragment()
            .fragmentManager(getFragmentManager())
            .containerId(R.id.container_main)
            .backStackEnabled(true)
            .build());
   }

   @Override
   public void onLikeClicked(TripModel tripModel) {
      getPresenter().likeItem(tripModel);
   }

   @Override
   public void onAddToBucketClicked(TripModel tripModel) {
      getPresenter().addItemToBucket(tripModel);
   }

   @Override
   public void onCellClicked(TripModel model) {
      getPresenter().openTrip(model);
   }

   @Override
   public void moveToTripDetails(TripModel model) {
      router.moveTo(FeedEntityDetailsFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create()
                  .visible(false)
                  .build())
            .data(new FeedEntityDetailsBundle.Builder().feedItem(FeedItem.create(model, null))
                  .showAdditionalInfo(true)
                  .build())
            .build());
   }
}
