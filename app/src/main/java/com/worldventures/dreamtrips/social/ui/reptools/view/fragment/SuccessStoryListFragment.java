package com.worldventures.dreamtrips.social.ui.reptools.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.badoo.mobile.util.WeakHandler;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.SuccessStoryListPresenter;
import com.worldventures.dreamtrips.social.ui.reptools.view.adapter.SuccessStoryHeaderAdapter;
import com.worldventures.dreamtrips.social.ui.reptools.view.cell.SuccessStoryCell;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_success_stories)
public class SuccessStoryListFragment extends BaseFragment<SuccessStoryListPresenter> implements SwipeRefreshLayout.OnRefreshListener, SuccessStoryListPresenter.View {

   private static final String EXTRA_IS_SEARCH_ICONIFIED = "extra_is_search_iconified";
   private static final String EXTRA_IS_SEARCH_FOCUSED = "extra_is_search_focused";

   @InjectView(R.id.recyclerViewStories) protected EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.detail_container) protected FrameLayout flDetailContainer;
   @InjectView(R.id.search) protected SearchView search;
   @InjectView(R.id.ll_empty_view) protected ViewGroup emptyView;

   private BaseDelegateAdapter<SuccessStory> adapter;
   private final WeakHandler weakHandler = new WeakHandler();

   @Override
   protected SuccessStoryListPresenter createPresenter(Bundle savedInstanceState) {
      return new SuccessStoryListPresenter();
   }

   @OnClick(R.id.iv_filter)
   public void onActionFilter() {
      getPresenter().onShowFilterRequired();
   }

   @Override
   public void showFilterDialog(boolean showFavorites) {
      View menuItemView = getActivity().findViewById(R.id.iv_filter);
      PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
      popupMenu.inflate(R.menu.menu_success_stories_filter);
      popupMenu.getMenu().getItem(showFavorites ? 1 : 0).setChecked(true);
      popupMenu.setOnMenuItemClickListener((menuItem) -> {
         switch (menuItem.getItemId()) {
            case R.id.action_show_all:
               getPresenter().filterFavorites(false);
               break;
            case R.id.action_show_favorites:
               getPresenter().filterFavorites(true);
               break;
            default:
               break;
         }
         return false;
      });
      popupMenu.show();
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (outState == null || search == null) {
         return;
      }
      outState.putBoolean(EXTRA_IS_SEARCH_ICONIFIED, search.isIconified());
      outState.putBoolean(EXTRA_IS_SEARCH_FOCUSED, search.isFocused());
   }

   @Override
   public void onViewStateRestored(Bundle savedInstanceState) {
      super.onViewStateRestored(savedInstanceState);
      if (savedInstanceState == null) {
         return;
      }
      search.setIconified(savedInstanceState.getBoolean(EXTRA_IS_SEARCH_ICONIFIED, true));
      if (savedInstanceState.getBoolean(EXTRA_IS_SEARCH_FOCUSED, false)) {
         search.requestFocus();
      }
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      flDetailContainer.setVisibility(isTabletLandscape() ? View.VISIBLE : View.GONE);
      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(SuccessStory.class, SuccessStoryCell.class);
      adapter.registerDelegate(SuccessStory.class, new SuccessStoryCell.Delegate() {
         @Override
         public void onCellClicked(SuccessStory model, int position) {
            search.clearFocus();
            getPresenter().onSuccessStoryCellClick(model, position);
         }
      });
      adapter.setHasStableIds(true);
      recyclerView.setEmptyView(emptyView);
      recyclerView.setAdapter(adapter);
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

      StickyHeadersItemDecoration decoration = new StickyHeadersBuilder().setAdapter(adapter)
            .setRecyclerView(recyclerView)
            .setStickyHeadersAdapter(new SuccessStoryHeaderAdapter(adapter.getItems(), R.layout.adapter_item_succes_story_header), false)
            .build();
      recyclerView.addItemDecoration(decoration);
      search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String query) {
            return false;
         }

         @Override
         public boolean onQueryTextChange(String query) {
            getPresenter().filterByQuery(query);
            return false;
         }
      });
      search.setOnClickListener(v -> getPresenter().onSearchActivated());
      search.setIconifiedByDefault(true);
   }

   @Override
   public void onDestroyView() {
      this.recyclerView.setAdapter(null);
      super.onDestroyView();
   }

   @Override
   public void onRefresh() {
      getPresenter().reload();
   }

   @Override
   public void setItems(@NonNull List<SuccessStory> items) {
      adapter.clear();
      adapter.addItems(new ArrayList<>(items));
      adapter.notifyDataSetChanged();
   }

   @Override
   public void finishLoading() {
      weakHandler.post(() -> {
         if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
         }
         openFirst();
      });
   }

   private void openFirst() {
      if (refreshLayout != null) {
         weakHandler.post(() -> {
            if (isTabletLandscape() && adapter.getCount() > 0) {
               getPresenter().openFirst(adapter.getItem(0));
            }
         });
      }
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
   public void openStory(@NonNull Bundle bundle) {
      if (isTabletLandscape()) {
         bundle.putBoolean(SuccessStoryDetailsFragment.EXTRA_SLAVE, true);
         router.moveTo(SuccessStoryDetailsFragment.class, NavigationConfigBuilder.forFragment()
               .backStackEnabled(true)
               .fragmentManager(getChildFragmentManager())
               .containerId(R.id.detail_container)
               .data(bundle)
               .build());
      } else {
         router.moveTo(SuccessStoryDetailsFragment.class, NavigationConfigBuilder.forActivity().data(bundle).build());
      }

   }
}
