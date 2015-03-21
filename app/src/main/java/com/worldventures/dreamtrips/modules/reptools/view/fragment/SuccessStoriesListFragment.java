package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoriesListFragmentPM;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.reptools.view.adapter.SuccessStoryHeaderAdapter;
import com.worldventures.dreamtrips.modules.reptools.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_success_stories)
public class SuccessStoriesListFragment extends BaseFragment<SuccessStoriesListFragmentPM> implements SwipeRefreshLayout.OnRefreshListener, SuccessStoriesListFragmentPM.View {

    @InjectView(R.id.recyclerViewTrips)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.detail_container)
    FrameLayout flDetailContainer;
    FilterableArrayListAdapter adapter;

    @InjectView(R.id.iv_search)
    SearchView ivSearch;

    @InjectView(R.id.iv_filter)
    ImageView ivFilter;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;


    @Override
    protected SuccessStoriesListFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new SuccessStoriesListFragmentPM(this);
    }

    @OnClick(R.id.iv_filter)
    public void onActionFilter() {
        View menuItemView = getActivity().findViewById(R.id.iv_filter);
        PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
        popupMenu.inflate(R.menu.menu_success_stories_filter);
        boolean isFavorites = getPresentationModel().isFilterFavorites();
        popupMenu.getMenu().getItem(isFavorites ? 1 : 0).setChecked(true);
        popupMenu.setOnMenuItemClickListener((menuItem) -> {
            getPresentationModel().reloadWithFilter(menuItem.getItemId());
            return false;
        });
        popupMenu.show();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        this.adapter = new FilterableArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
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
                .setStickyHeadersAdapter(new SuccessStoryHeaderAdapter(adapter.getItems()), false)
                .build();

        recyclerView.addItemDecoration(decoration);

        ivSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    }

    @Override
    public void onRefresh() {
        getPresentationModel().reload();
    }

    @Override
    public boolean isTablet() {
        return ViewUtils.isTablet(getActivity());
    }

    @Override
    public boolean isLandscape() {
        return ViewUtils.isLandscapeOrientation(getActivity());
    }

    @Override
    public void setDetailsContainerVisibility(boolean b) {
        flDetailContainer.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public IRoboSpiceAdapter<SuccessStory> getAdapter() {
        return adapter;
    }

    @Override
    public void finishLoading(List<SuccessStory> result) {
        new Handler().postDelayed(() -> {
            if (getActivity() != null) {
                refreshLayout.setRefreshing(false);
                if (isLandscape() && isTablet()) {
                    if (!result.isEmpty()) {
                        getEventBus().post(new OnSuccessStoryCellClickEvent(result.get(0), 1));
                    }
                }
            }
        }, 500);
    }

    @Override
    public void startLoading() {
        new Handler().postDelayed(() -> refreshLayout.setRefreshing(true), 100);
    }

    @Override
    public void showOnlyFavorites(boolean onlyFavorites) {
        //TODO
    }

}
