package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedHashtagBundle;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedHashtagPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.HashtagSuggestionCell;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.HashtagSuggestionUtil;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

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
    @Inject
    @ForActivity
    Injector injector;
    @State
    boolean searchOpened;
    @State
    String query;

    @InjectView(R.id.suggestions)
    RecyclerView suggestions;
    @Inject
    FragmentWithFeedDelegate fragmentWithFeedDelegate;

    BaseDelegateAdapter<HashtagSuggestion> suggestionAdapter;
    RecyclerViewStateDelegate stateDelegate;

    private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
    private Bundle savedInstanceState;
    private SearchView searchView;
    private EditText searchText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        BaseDelegateAdapter feedAdapter = new BaseDelegateAdapter<>(getContext(), this);
        statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
        statePaginatedRecyclerViewManager.stateRecyclerView.setEmptyView(emptyView);
        statePaginatedRecyclerViewManager.init(feedAdapter, savedInstanceState);
        statePaginatedRecyclerViewManager.setOnRefreshListener(this);
        statePaginatedRecyclerViewManager.setPaginationListener(() -> getPresenter().loadNext());
        fragmentWithFeedDelegate.init(feedAdapter);

        suggestionAdapter = new BaseDelegateAdapter<>(getActivity(), injector);
        suggestionAdapter.registerCell(HashtagSuggestion.class, HashtagSuggestionCell.class);
        suggestionAdapter.registerDelegate(HashtagSuggestion.class, new HashtagSuggestionCell.Delegate() {
            public void onCellClicked(HashtagSuggestion model) {
                onSuggestionClicked(model.getName());
            }
        });

        stateDelegate.setRecyclerView(suggestions);

        suggestions.setAdapter(suggestionAdapter);
        suggestions.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        suggestions.addItemDecoration(dividerItemDecoration());

        //make root focusable for shifting focus ToolBar.SearchView -> rootView
        rootView.setFocusable(true);
        rootView.setFocusableInTouchMode(true);
    }

    private void onSuggestionClicked(String suggestion) {
        if (searchText != null) {
            String descriptionText = searchText.getText().toString();
            int endReplace = searchText.getSelectionStart();

            int startReplace = HashtagSuggestionUtil.calcStartPosBeforeReplace(descriptionText, endReplace);
            String newText = HashtagSuggestionUtil.generateText(descriptionText, suggestion, endReplace);

            searchText.setText(newText);
            searchText.setSelection(startReplace + HashtagSuggestionUtil.replaceableText(suggestion).length());
            clearSuggestions();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        stateDelegate.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        stateDelegate.saveStateIfNeeded(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        releaseSearchFocus(searchView);
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

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchView.setQuery(query, false);
        searchView.setOnCloseListener(() -> false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FeedHashtagFragment.this.query = query;
                releaseSearchFocus(MenuItemCompat.getActionView(searchItem));
                getPresenter().onRefresh();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                clearSuggestions();
                getPresenter().query(getTextFromCursor());
                return true;
            }
        });

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
                getActivity().onBackPressed();
                return true;
            }
        });

        if (args != null && args.getHashtag() != null) {
            releaseSearchFocus(MenuItemCompat.getActionView(searchItem));
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
        if (!statePaginatedRecyclerViewManager.isNoMoreElements())
            fragmentWithFeedDelegate.addItem(new LoadMoreModel());
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
        if (emptyView != null) emptyView.setVisibility(View.GONE);
    }

    @Override
    public void finishLoading() {
        statePaginatedRecyclerViewManager.finishLoading();
        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuggestionsReceived(@NonNull List<HashtagSuggestion> suggestionList) {
        suggestionAdapter.clear();
        suggestionAdapter.addItems(suggestionList);
        if (!suggestionList.isEmpty()) {
            suggestions.setVisibility(View.VISIBLE);
        } else {
            suggestions.setVisibility(View.GONE);
        }
    }

    @Override
    public void clearSuggestions() {
        suggestionAdapter.clear();
        suggestions.setVisibility(View.GONE);
    }

    private void releaseSearchFocus(@Nullable View search){
        new WeakHandler().postDelayed(() -> {
            if (search != null) search.clearFocus();
            if (getView() != null) getView().requestFocus(); //check for multiple fast device rotation
        }, 50);
    }

    private DividerItemDecoration dividerItemDecoration() {
        DividerItemDecoration decor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        decor.setLeftMarginRes(R.dimen.spacing_normal);
        return decor;
    }

    private String getTextFromCursor() {
        String text = searchText.getText().toString();
        if (text.lastIndexOf(" ") == text.length() - 1) return "";
        else return text.replaceAll("^.*?(\\w+)\\W*$", "$1");
    }
}
