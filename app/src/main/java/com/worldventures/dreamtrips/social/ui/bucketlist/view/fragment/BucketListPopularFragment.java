package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketPopularTabsPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.util.SweetDialogHelper;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.delegate.BucketPopularCellDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_bucket_popular)
@MenuResource(R.menu.menu_bucket_popular)
public class BucketListPopularFragment extends RxBaseFragment<BucketPopularPresenter>
      implements BucketPopularPresenter.View, SwipeRefreshLayout.OnRefreshListener,
      BucketPopularCellDelegate, SelectablePagerFragment {

   @InjectView(R.id.recyclerViewBuckets) protected EmptyRecyclerView recyclerView;

   @InjectView(R.id.ll_empty_view) protected View emptyView;

   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;

   private SweetDialogHelper sweetDialogHelper;

   private FilterableArrayListAdapter<PopularBucketItem> adapter;
   private RecyclerViewStateDelegate stateDelegate;

   private WeakHandler weakHandler;

   @Override
   protected BucketPopularPresenter createPresenter(Bundle savedInstanceState) {
      BucketItem.BucketType type = (BucketItem.BucketType) getArguments().getSerializable(BucketPopularTabsPresenter.Companion
            .getEXTRA_TYPE());
      return new BucketPopularPresenter(type);
   }

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
      this.recyclerView.setLayoutManager(getLayoutManager());
      this.recyclerView.setEmptyView(emptyView);
      this.adapter = new FilterableArrayListAdapter<>(getActivity(), this);
      this.adapter.registerCell(PopularBucketItem.class, BucketPopularCell.class);
      this.adapter.registerDelegate(PopularBucketItem.class, this);
      this.recyclerView.setAdapter(this.adapter);
      stateDelegate.setRecyclerView(recyclerView);

      this.refreshLayout.setOnRefreshListener(this);
      this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

      sweetDialogHelper = new SweetDialogHelper();
   }

   @Override
   public void onDestroyView() {
      stateDelegate.onDestroyView();
      this.recyclerView.setAdapter(null);
      super.onDestroyView();
   }

   @Override
   public void onSelectedFromPager() {
      getPresenter().onSelected();
   }

   private RecyclerView.LayoutManager getLayoutManager() {
      if (isTabletLandscape()) {
         return new GridLayoutManager(getActivity(), 3);
      } else {
         return new LinearLayoutManager(getActivity());
      }
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      MenuItem searchItem = menu.findItem(R.id.action_search);
      SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      searchView.setQueryHint(getString(R.string.search));
      searchView.setIconifiedByDefault(false);
      searchView.setOnCloseListener(() -> {
         getPresenter().searchClosed();
         return false;
      });
      searchView.setOnQueryTextListener(onQueryTextListener);
   }

   private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
         return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
         getPresenter().onSearch(newText);
         adapter.setFilter(newText);
         return false;
      }
   };

   @Override
   public int getItemsCount() {
      return adapter.getCount();
   }

   @Override
   public void setItems(List<? extends PopularBucketItem> items) {
      adapter.setItems(new ArrayList(items));
   }

   @Override
   public void setFilteredItems(List<? extends PopularBucketItem> items) {
      adapter.setFilteredItems(new ArrayList<>(items));
   }

   @Override
   public void flushFilter() {
      adapter.flushFilter();
   }

   @Override
   public void removeItem(PopularBucketItem item) {
      adapter.remove(item);
   }

   @Override
   public void notifyItemsChanged() {
      adapter.notifyDataSetChanged();
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
   public void notifyItemWasAddedToBucketList(BucketItem bucketItem) {
      sweetDialogHelper.notifyItemAddedToBucket(getActivity(), bucketItem);
   }

   @Override
   public void onRefresh() {
      if (!TextUtils.isEmpty(adapter.getQuery())) {
         getPresenter().onSearch(adapter.getQuery());
      } else {
         getPresenter().reload();
      }
   }

   @Override
   public void onCellClicked(PopularBucketItem model) {
      //do nothing
   }

   @Override
   public void addClicked(PopularBucketItem popularBucketItem, int position) {
      getPresenter().onAdd(popularBucketItem);
   }

   @Override
   public void doneClicked(PopularBucketItem popularBucketItem, int position) {
      getPresenter().onDone(popularBucketItem);
   }
}
