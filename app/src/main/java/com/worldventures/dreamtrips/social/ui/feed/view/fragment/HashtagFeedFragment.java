package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.SoftInputUtil;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.custom.ProgressEmptyRecyclerView;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.bundle.HashtagFeedBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.social.ui.feed.presenter.HashtagFeedPresenter;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.HashtagSuggestionCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.FeedCellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.SideMarginsItemDecorator;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FocusableStatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.util.HashtagSuggestionUtil;
import com.worldventures.dreamtrips.social.ui.membership.view.util.DividerItemDecoration;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@MenuResource(R.menu.menu_hashtag_feed)
@Layout(R.layout.fragment_hashtag_feed)
public class HashtagFeedFragment extends RxBaseFragmentWithArgs<HashtagFeedPresenter, HashtagFeedBundle>
      implements HashtagFeedPresenter.View, SwipeRefreshLayout.OnRefreshListener, FeedEntityEditingView {

   @InjectView(R.id.empty_view) ViewGroup emptyView;
   @InjectView(R.id.suggestionProgress) View suggestionProgressBar;
   @InjectView(R.id.suggestions) ProgressEmptyRecyclerView suggestions;

   @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;
   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;

   BaseDelegateAdapter<HashtagSuggestion> suggestionAdapter;
   RecyclerViewStateDelegate stateDelegate;

   private FocusableStatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private Bundle savedInstanceState;
   private SearchView searchView;
   private EditText searchText;
   private MenuItem searchItem;
   private int cursorPositionWhenTextChanged;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      BaseDelegateAdapter feedAdapter = new BaseDelegateAdapter<>(getContext(), this);
      statePaginatedRecyclerViewManager = new FocusableStatePaginatedRecyclerViewManager(rootView.findViewById(R.id.recyclerView),
            rootView.findViewById(R.id.swipe_container));
      statePaginatedRecyclerViewManager.getStateRecyclerView().setEmptyView(emptyView);
      statePaginatedRecyclerViewManager.init(feedAdapter, savedInstanceState);
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.setPaginationListener(() -> {
         if (!statePaginatedRecyclerViewManager.isNoMoreElements()) {
            getPresenter().loadNext();
         }
      });
      if (ViewUtils.isTablet(getContext())) {
         int margin = getResources().getInteger(R.integer.feed_landscape_horizontal_margin);
         statePaginatedRecyclerViewManager.addItemDecoration(new SideMarginsItemDecorator(margin, false));
      }

      fragmentWithFeedDelegate.init(feedAdapter);
      BaseFeedCell.FeedCellDelegate delegate = new FeedCellDelegate(getPresenter());
      fragmentWithFeedDelegate.registerDelegate(PhotoFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(TripFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(BucketFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(PostFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(VideoFeedItem.class, delegate);

      suggestionAdapter = new BaseDelegateAdapter<>(getActivity(), this);
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
      suggestions.setProgressView(suggestionProgressBar);
      //make root focusable for shifting focus ToolBar.SearchView -> rootView
      rootView.setFocusable(true);
      rootView.setFocusableInTouchMode(true);
   }

   @Override
   public void onPause() {
      super.onPause();
      statePaginatedRecyclerViewManager.stopAutoplayVideos();
   }

   private void startAutoplayVideos() {
      statePaginatedRecyclerViewManager.startLookingForCompletelyVisibleItem(bindUntilPauseComposer());
   }

   private void onSuggestionClicked(String suggestion) {
      if (searchText != null) {
         String descriptionText = searchText.getText().toString();
         int endReplace = cursorPositionWhenTextChanged;

         int startReplace = HashtagSuggestionUtil.calcStartPosBeforeReplace(descriptionText, endReplace);
         String newText = HashtagSuggestionUtil.generateText(descriptionText, suggestion, endReplace);

         searchText.setText(newText);
         searchText.setSelection(startReplace + HashtagSuggestionUtil.replaceableText(suggestion).length());
         searchPosts(searchText.getText().toString());
      }
   }

   @Override
   public void onStop() {
      super.onStop();
      fragmentWithFeedDelegate.resetTranslatedStatus();
   }

   public void onDestroyView() {
      SoftInputUtil.hideSoftInputMethod(searchView);
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
      startAutoplayVideos();
      releaseSearchFocus(searchView);

      HashtagFeedBundle args = getArgs();
      if (args != null && args.getHashtag() != null) {
         getPresenter().onRefresh();
      }

      activeFeedRouteInteractor.activeFeedRouteCommandActionPipe()
            .send(ActiveFeedRouteCommand.update(FeedCellListWidthProvider.FeedType.TIMELINE));
   }

   @Override
   protected HashtagFeedPresenter createPresenter(Bundle savedInstanceState) {
      HashtagFeedBundle args = getArgs();
      String query = args != null && !TextUtils.isEmpty(args.getHashtag()) ? args.getHashtag() : null;
      HashtagFeedPresenter presenter = new HashtagFeedPresenter();
      presenter.setQuery(query);
      return presenter;
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);

      searchItem = menu.findItem(R.id.action_search);
      searchItem.expandActionView();

      searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      searchText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
      searchView.setQuery(getPresenter().getQuery(), false);
      searchView.setOnCloseListener(() -> false);
      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String query) {
            searchPosts(query);
            return true;
         }

         @Override
         public boolean onQueryTextChange(String fullQueryText) {
            getPresenter().setQuery(fullQueryText);
            clearSuggestions();
            getPresenter().searchSuggestions(fullQueryText, getTextFromCursor());
            cursorPositionWhenTextChanged = searchText.getSelectionStart();
            return true;
         }
      });

      MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
         @Override
         public boolean onMenuItemActionExpand(MenuItem item) {
            searchView.setQuery(getPresenter().getQuery(), false);
            return true;
         }

         @Override
         public boolean onMenuItemActionCollapse(MenuItem item) {
            getPresenter().setQuery(null);
            searchView.setQuery("", false);
            getActivity().onBackPressed();
            return true;
         }
      });

      releaseSearchFocus(MenuItemCompat.getActionView(searchItem));
   }

   private void searchPosts(String query) {
      getPresenter().setQuery(query);
      if (searchItem != null) {
         releaseSearchFocus(MenuItemCompat.getActionView(searchItem));
      }
      getPresenter().onRefresh();
      clearSuggestions();
      getPresenter().cancelLastSuggestionRequest();
   }

   @Override
   public void onRefresh() {
      getPresenter().onRefresh();
   }

   @Override
   public void refreshFeedItems(List feedItems) {
      fragmentWithFeedDelegate.updateItems(feedItems, statePaginatedRecyclerViewManager.getStateRecyclerView());
   }

   @Override
   public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
      statePaginatedRecyclerViewManager.updateLoadingStatus(loading, noMoreElements);
   }

   @Override
   public void updateItem(FeedItem feedItem) {
      fragmentWithFeedDelegate.notifyItemChanged(feedItem);
   }

   @Override
   public void showLoading() {
      fragmentWithFeedDelegate.addItem(new LoadMoreModel());
      fragmentWithFeedDelegate.notifyItemInserted(fragmentWithFeedDelegate.getItems().size() - 1);
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
   public void onSuggestionsReceived(String fullQueryText, @NonNull List<HashtagSuggestion> suggestionList) {
      suggestionAdapter.clear();
      if ((searchText == null || fullQueryText.equals(searchText.getText().toString())) && !suggestionList.isEmpty()) {
         suggestions.setVisibility(View.VISIBLE);
         suggestionAdapter.addItems(suggestionList);
      } else {
         suggestions.setVisibility(View.GONE);
      }
   }

   @Override
   public void clearSuggestions() {
      suggestionAdapter.clear();
      suggestions.setVisibility(View.GONE);
   }

   @Override
   public void openEditTextualPost(TextualPost textualPost) {
      fragmentWithFeedDelegate.openTextualPostEdit(getActivity().getSupportFragmentManager(), textualPost);
   }

   @Override
   public void openEditPhoto(Photo photo) {
      fragmentWithFeedDelegate.openPhotoEdit(getActivity().getSupportFragmentManager(), photo);
   }

   @Override
   public void openEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type) {
      fragmentWithFeedDelegate.openBucketEdit(getActivity().getSupportFragmentManager(), isTabletLandscape(), new BucketBundle(bucketItem, type));
   }

   @Override
   public void showSuggestionProgress() {
      if (suggestions != null) {
         suggestions.showProgress();
      }
   }

   @Override
   public void hideSuggestionProgress() {
      if (suggestions != null) {
         suggestions.hideProgress();
      }
   }

   private void releaseSearchFocus(@Nullable View search) {
      new WeakHandler().postDelayed(() -> {
         if (search != null) {
            search.clearFocus();
         }
         if (getView() != null) {
            getView().requestFocus(); //check for multiple fast device rotation
         }
      }, 50);
   }

   private DividerItemDecoration dividerItemDecoration() {
      DividerItemDecoration decor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
      decor.setShowDividerAfterLastCell(true);
      return decor;
   }

   private String getTextFromCursor() {
      String text = searchText.getText().toString();
      int cursorPosition = searchText.getSelectionStart();
      int startPosition = HashtagSuggestionUtil.calcStartPosBeforeReplace(text, cursorPosition);
      return text.substring(startPosition, cursorPosition);
   }

   @Override
   public void flagSentSuccess() {
      informUser(R.string.flag_sent_success_msg);
   }

   @Override
   public void showComments(FeedItem feedItem) {
      fragmentWithFeedDelegate.openComments(feedItem, isVisibleOnScreen(), isTabletLandscape());
   }
}
