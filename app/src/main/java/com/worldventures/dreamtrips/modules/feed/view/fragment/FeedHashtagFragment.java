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
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedHashtagBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedHashtagPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;

import java.util.List;

import butterknife.InjectView;
import icepick.Icepick;
import icepick.State;

@MenuResource(R.menu.menu_hashtag_feed)
@Layout(R.layout.fragment_hashtag_feed)
public class FeedHashtagFragment extends RxBaseFragmentWithArgs<FeedHashtagPresenter, FeedHashtagBundle> implements FeedHashtagPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.feeds)
    FeedView feedView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.empty_view)
    ViewGroup emptyView;
    @InjectView(R.id.suggestions)
    RecyclerView suggestions;

    @State
    boolean searchOpened;
    @State
    String query;

    private WeakHandler weakHandler;
    private Bundle savedInstanceState;
    private BaseArrayListAdapter feedAdapter;
    private BaseArrayListAdapter suggestionAdapter;
    private RecyclerViewStateDelegate stateDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        weakHandler = new WeakHandler();
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(feedView);
        feedAdapter = new BaseDelegateAdapter<>(feedView.getContext(), this);
        feedView.setup(savedInstanceState, feedAdapter);
        feedView.setEmptyView(emptyView);
        feedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = recyclerView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = feedView.getLayoutManager().findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
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
                getPresenter().loadFeeds();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                return true;
            }
        });

        if (args.getHashtag() != null) {
            new WeakHandler().postDelayed(() -> SoftInputUtil.hideSoftInputMethod(getActivity()), 50);
            getPresenter().loadFeeds();
        }
    }

    @Override
    public void onRefresh() {
        getPresenter().reloadFeeds();
    }

    @Override
    public void addFeedItems(List items) {
        int itemsCount = feedAdapter.getCount() - 1;
        feedAdapter.addItems(items);
        feedAdapter.notifyItemRangeInserted(itemsCount, items.size());
    }

    @Override
    public void clearFeedItems() {
        feedAdapter.clear();
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void addSuggestionItems(List items) {
        int itemsCount = suggestionAdapter.getCount() - 1;
        suggestionAdapter.addItems(items);
        suggestionAdapter.notifyItemRangeInserted(itemsCount, items.size());
    }

    @Override
    public void clearSuggestionItems() {
        suggestionAdapter.clear();
        suggestionAdapter.notifyDataSetChanged();
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
    }

}
