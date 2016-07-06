package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedHashtagBundle;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedHashtagPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import icepick.Icepick;
import icepick.State;

@MenuResource(R.menu.menu_hashtag_feed)
@Layout(R.layout.fragment_hashtag_feed)
public class FeedHashtagFragment extends RxBaseFragmentWithArgs<FeedHashtagPresenter, FeedHashtagBundle> implements FeedHashtagPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.empty_view)
    ViewGroup emptyView;
    @InjectView(R.id.suggestions)
    RecyclerView suggestions;

    @State
    boolean searchOpened;
    @State
    String query;

    @Inject
    FragmentWithFeedDelegate fragmentWithFeedDelegate;

//    private BaseArrayListAdapter suggestionAdapter;

    private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
    private Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        BaseDelegateAdapter adapter = new BaseDelegateAdapter<>(getContext(), this);
        statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
        statePaginatedRecyclerViewManager.stateRecyclerView.setEmptyView(emptyView);
        statePaginatedRecyclerViewManager.init(adapter, savedInstanceState);
        statePaginatedRecyclerViewManager.setOnRefreshListener(this);
        statePaginatedRecyclerViewManager.setPaginationListener(() -> getPresenter().loadNext());
        fragmentWithFeedDelegate.init(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected FeedHashtagPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedHashtagPresenter();
    }

    @Override
    protected void onMenuInflated(Menu menu) {
        super.onMenuInflated(menu);
        FeedHashtagBundle args = getArgs();

        query = query != null ? query : (args != null && !TextUtils.isEmpty(args.getHashtag()) ? args.getHashtag() : null);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (query != null || searchOpened) searchItem.expandActionView();
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.setQuery(query, false);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchOpened = true;
                searchView.setQuery(query, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchOpened = false;
                query = null;
                searchView.setQuery("", false);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FeedHashtagFragment.this.query = query;
                getPresenter().onRefresh();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                return true;
            }
        });

        if (args != null && args.getHashtag() != null) {
            new WeakHandler().postDelayed(() -> SoftInputUtil.hideSoftInputMethod(getActivity()), 50);
            getPresenter().onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void refreshFeedItems(List feedItems) {
        fragmentWithFeedDelegate.clearItems();
        fragmentWithFeedDelegate.addItems(feedItems);
        if (!statePaginatedRecyclerViewManager.isNoMoreElements()) fragmentWithFeedDelegate.addItem(new LoadMoreModel());
        fragmentWithFeedDelegate.notifyDataSetChanged();
    }

    @Override
    public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
        statePaginatedRecyclerViewManager.updateLoadingStatus(loading, noMoreElements);
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public void startLoading() {
        statePaginatedRecyclerViewManager.startLoading();
    }

    @Override
    public void finishLoading() {
        statePaginatedRecyclerViewManager.finishLoading();
    }

}
