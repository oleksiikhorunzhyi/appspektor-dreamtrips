package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.badoo.mobile.util.WeakHandler;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryListPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.adapter.HeaderAdapter;
import com.worldventures.dreamtrips.modules.reptools.view.cell.SuccessStoryCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_success_stories)
public class SuccessStoryListFragment extends BaseFragment<SuccessStoryListPresenter> implements SwipeRefreshLayout.OnRefreshListener, SuccessStoryListPresenter.View {

    @InjectView(R.id.recyclerViewStories)
    protected EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.detail_container)
    protected FrameLayout flDetailContainer;
    @InjectView(R.id.search)
    protected SearchView search;
    @InjectView(R.id.iv_filter)
    protected ImageView ivFilter;
    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    private FilterableArrayListAdapter<SuccessStory> adapter;

    private WeakHandler weakHandler = new WeakHandler();

    @Override
    protected SuccessStoryListPresenter createPresenter(Bundle savedInstanceState) {
        return new SuccessStoryListPresenter();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        flDetailContainer.setVisibility(isTabletLandscape() ? View.VISIBLE : View.GONE);
        recyclerView.postDelayed(() -> {
            if (recyclerView != null) {
                adapter.notifyDataSetChanged();
            }
        }, 100);
        openFirst();
    }

    @OnClick(R.id.iv_filter)
    public void onActionFilter() {
        View menuItemView = getActivity().findViewById(R.id.iv_filter);
        PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
        popupMenu.inflate(R.menu.menu_success_stories_filter);
        boolean isFavorites = getPresenter().isFilterFavorites();
        popupMenu.getMenu().getItem(isFavorites ? 1 : 0).setChecked(true);
        popupMenu.setOnMenuItemClickListener((menuItem) -> {
            getPresenter().reloadWithFilter(menuItem.getItemId());
            return false;
        });
        popupMenu.show();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        flDetailContainer.setVisibility(isTabletLandscape() ? View.VISIBLE : View.GONE);

        this.adapter = new FilterableArrayListAdapter<>(getActivity(), injectorProvider);
        this.adapter.registerCell(SuccessStory.class, SuccessStoryCell.class);
        this.adapter.setHasStableIds(true);
        this.recyclerView.setEmptyView(emptyView);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(recyclerView)
                .setStickyHeadersAdapter(new HeaderAdapter(adapter.getItems(),
                        R.layout.adapter_item_succes_story_header), false)
                .build();

        recyclerView.addItemDecoration(decoration);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.setFilter(s);
                return false;
            }
        });
        search.setIconifiedByDefault(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public FilterableArrayListAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void finishLoading(List<SuccessStory> result) {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
            openFirst();
        });
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return getChildFragmentManager();
    }

    private void openFirst() {
        if (refreshLayout != null)
            weakHandler.post(() -> {
                if (isTabletLandscape() && adapter.getCount() > 0) {
                    getPresenter().openFirst(adapter.getItem(0));
                }
            });
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void onStoryClicked() {
        search.clearFocus();
    }
}
