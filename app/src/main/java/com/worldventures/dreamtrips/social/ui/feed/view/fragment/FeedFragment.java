package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.model.Circle;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeImageView;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.cell.EmptyFeedModel;
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.EmptyFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.FeedCellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.SuggestedPhotosDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.UploadingCellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.social.ui.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.social.ui.profile.model.ReloadFeedModel;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import rx.subjects.PublishSubject;

@Layout(R.layout.fragment_feed)
@MenuResource(R.menu.menu_activity_feed)
public class FeedFragment extends RxBaseFragmentWithArgs<FeedPresenter, FeedBundle> implements FeedPresenter.View,
      SwipeRefreshLayout.OnRefreshListener, SuggestedPhotosDelegate, FeedEntityEditingView {

   @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;
   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;

   @InjectView(R.id.posting_header) View postingHeader;
   @InjectView(R.id.additional_info_container) View additionalInfoContainer;

   private BadgeImageView friendsBadge;
   private BadgeImageView unreadConversationBadge;

   private CirclesFilterPopupWindow filterPopupWindow;

   private ContentObserver contentObserver;
   private PublishSubject<Void> contentObserverSubject = PublishSubject.create();

   private StatePaginatedRecyclerViewManager recyclerViewManager;
   private Bundle savedInstanceState;

   private MaterialDialog blockingProgressDialog;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      setupUi();
   }

   @Override
   public void onStop() {
      super.onStop();
      fragmentWithFeedDelegate.resetTranslatedStatus();
   }

   @Override
   public void onDetach() {
      super.onDetach();
      filterPopupWindow = null;
   }

   @Override
   public void onDestroyView() {
      if (contentObserver != null) {
         getContext().getContentResolver().unregisterContentObserver(contentObserver);
      }
      super.onDestroyView();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      BaseDelegateAdapter adapter = new BaseDelegateAdapter<>(getContext(), this);
      // TODO: 2/23/17 put pagination logic into common set of presenter interfaces and view delegates
      // when feed storage refactoring is merged
      recyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
      recyclerViewManager.init(adapter, savedInstanceState);
      recyclerViewManager.setOnRefreshListener(this);
      recyclerViewManager.setPaginationListener(() -> {
         if (!recyclerViewManager.isNoMoreElements() && getPresenter().loadNext()) {
            fragmentWithFeedDelegate.addItem(new LoadMoreModel());
            fragmentWithFeedDelegate.notifyItemInserted(fragmentWithFeedDelegate.getItems().size() - 1);
         }
      });

      setupUi();

      fragmentWithFeedDelegate.init(adapter);
      registerAdditionalCells();
      registerCellDelegates();
   }

   private void setupUi() {
      if (isTabletLandscape()) {
         additionalInfoContainer.setVisibility(View.VISIBLE);
         postingHeader.setVisibility(View.GONE);
         fragmentWithFeedDelegate.openFeedAdditionalInfo(getChildFragmentManager(), getPresenter().getAccount());
      } else {
         additionalInfoContainer.setVisibility(View.GONE);
         postingHeader.setVisibility(View.VISIBLE);
         fragmentWithFeedDelegate.hideAdditonalInfo(getChildFragmentManager());
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      startAutoplayVideos();
      activeFeedRouteInteractor.activeFeedRouteCommandActionPipe().send(ActiveFeedRouteCommand.update(Route.FEED));
   }

   @Override
   public void onPause() {
      super.onPause();
      recyclerViewManager.stopAutoplayVideos();
   }

   private void startAutoplayVideos() {
      recyclerViewManager.startLookingForCompletelyVisibleItem(bindUntilResumeComposer());
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      MenuItem friendsItem = menu.findItem(R.id.action_friend_requests);
      friendsBadge = (BadgeImageView) MenuItemCompat.getActionView(friendsItem);
      friendsBadge.setOnClickListener(v -> {
         fragmentWithFeedDelegate.openFriends(new FriendMainBundle(FriendMainBundle.REQUESTS));
         getPresenter().onFriendsOpened();
      });

      MenuItem conversationItem = menu.findItem(R.id.action_unread_conversation);
      if (conversationItem != null) {
         unreadConversationBadge = (BadgeImageView) MenuItemCompat.getActionView(conversationItem);
         unreadConversationBadge.setImage(R.drawable.messenger_icon_white);
         unreadConversationBadge.setOnClickListener(v -> getPresenter().onUnreadConversationsClick());
      }
      getPresenter().menuInflated();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_filter:
            if (filterPopupWindow == null || filterPopupWindow.dismissPassed()) {
               actionFilter();
            }
            return true;
         case R.id.action_search:
            fragmentWithFeedDelegate.openHashtagSearch();
            return true;
         default:
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   protected FeedPresenter createPresenter(Bundle savedInstanceState) {
      return new FeedPresenter();
   }

   @Optional
   @OnClick(R.id.share_post)
   protected void onPostClicked() {
      fragmentWithFeedDelegate.openPost(getActivity().getSupportFragmentManager());
   }

   @Override
   public void onAttachClicked() {
      getPresenter().attachSuggestionsClicked();
   }

   @Override
   public void openCreatePostScreen(MediaPickerAttachment mediaPickerAttachment) {
      CreateEntityBundle bundle = new CreateEntityBundle(mediaPickerAttachment, CreateEntityBundle.Origin.FEED);
      bundle.setShowPickerImmediately(false);
      fragmentWithFeedDelegate.openSharePhoto(getActivity().getSupportFragmentManager(), bundle);
   }

   @Override
   public void onCancelClicked() {
      getPresenter().removeSuggestedPhotos();
   }

   @Override
   public void onOpenProfileClicked() {
      fragmentWithFeedDelegate.openAccountProfile(getPresenter().getAccount());
   }

   @Override
   public void onSuggestionViewCreated(@NonNull SuggestedPhotoCellPresenterHelper.View view) {
      createSuggestionObserver();
      getPresenter().takeSuggestionView(view, savedInstanceState, contentObserverSubject.asObservable()
            .throttleLast(1, TimeUnit.SECONDS));
   }

   @Override
   public void onSyncViewState() {
      getPresenter().syncSuggestionViewState();
   }

   @Override
   public void onPreloadSuggestionPhotos(@NonNull PhotoPickerModel model) {
      getPresenter().preloadSuggestionChunk(model);
   }

   @Override
   public void onSelectPhoto(@NonNull PhotoPickerModel model) {
      getPresenter().selectPhoto(model);
   }

   @Override
   public long lastSyncTimestamp() {
      return getPresenter().lastSyncTimestamp();
   }

   @Override
   public void onCellClicked(SuggestedPhotosCell.SuggestedPhotoModel model) { }

   @Override
   public void updateItem(FeedItem feedItem) {
      fragmentWithFeedDelegate.notifyItemChanged(feedItem);
   }

   @Override
   public void setRequestsCount(int count) {
      if (friendsBadge != null) {
         friendsBadge.setBadgeValue(count);
      }
   }

   @Override
   public void setUnreadConversationCount(int count) {
      if (unreadConversationBadge != null) {
         unreadConversationBadge.setBadgeValue(count);
      }
   }

   @Override
   public void refreshFeedItems(List<FeedItem> feedItems, List<PostCompoundOperationModel> uploadingPostsList,
         boolean shouldShowSuggestions) {
      List feedModels = new ArrayList();
      processSuggestedPhotosItems(shouldShowSuggestions, feedModels);
      processUploadsInProgressItems(new UploadingPostsList(uploadingPostsList), feedModels);
      processFeedItems(feedItems, feedModels);
      fragmentWithFeedDelegate.updateItems(feedModels, recyclerViewManager.stateRecyclerView);
      startAutoplayVideos();
   }

   @Override
   public void dataSetChanged() {
      fragmentWithFeedDelegate.notifyDataSetChanged(recyclerViewManager.findFocusedPosition());
   }

   private void processSuggestedPhotosItems(boolean shouldShowSuggestions, List feedModels) {
      if (shouldShowSuggestions) {
         feedModels.add(new SuggestedPhotosCell.SuggestedPhotoModel());
      }
   }

   private void processUploadsInProgressItems(UploadingPostsList uploadingPostsList, List feedModels) {
      if (!uploadingPostsList.getPhotoPosts().isEmpty()) {
         feedModels.add(uploadingPostsList);
      }
   }

   private void processFeedItems(List<FeedItem> feedItems, List feedModels) {
      int feedItemsSize = feedItems == null ? 0 : feedItems.size();
      if (feedItemsSize == 0) {
         feedModels.add(new EmptyFeedModel());
      } else {
         feedModels.addAll(feedItems);
         if (postingHeader != null) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) postingHeader.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
            postingHeader.setLayoutParams(params);
         }
      }
   }

   @Override
   public void startLoading() {
      recyclerViewManager.startLoading();
   }

   @Override
   public void finishLoading() {
      recyclerViewManager.finishLoading();
   }

   @Override
   public void showBlockingProgress() {
      blockingProgressDialog = new MaterialDialog.Builder(getActivity()).progress(true, 0)
            .content(R.string.loading)
            .cancelable(false)
            .canceledOnTouchOutside(false)
            .show();
   }

   @Override
   public void hideBlockingProgress() {
      if (blockingProgressDialog != null) blockingProgressDialog.dismiss();
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
   public void updateLoadingStatus(boolean noMoreElements) {
      recyclerViewManager.updateLoadingStatus(false, noMoreElements);
   }

   @Override
   public void flagSentSuccess() {
      informUser(R.string.flag_sent_success_msg);
   }

   @Optional
   @OnClick(R.id.tv_search_friends)
   public void onFriendsSearchClicked() {
      fragmentWithFeedDelegate.openFriendsSearch();
   }

   @Override
   public void onRefresh() {
      getPresenter().refreshFeed();
   }

   @Override
   public void openComments(FeedItem feedItem) {
      fragmentWithFeedDelegate.openComments(feedItem, isVisibleOnScreen(), isTabletLandscape());
   }

   private void actionFilter() {
      if (getActivity().findViewById(R.id.action_filter) == null && getCollapseView() == null) return;

      getPresenter().actionFilter();
   }

   @Override
   public void showFilter(List<Circle> circles, Circle selectedCircle) {
      Collections.sort(circles);

      View menuItemView = getActivity().findViewById(R.id.action_filter);
      if (menuItemView == null) {
         menuItemView = getCollapseView();
      }

      filterPopupWindow = new CirclesFilterPopupWindow(getContext());
      filterPopupWindow.setCircles(circles);
      filterPopupWindow.setAnchorView(menuItemView);
      filterPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
         filterPopupWindow.dismiss();
         getPresenter().applyFilter((Circle) parent.getItemAtPosition(position));
      });
      filterPopupWindow.show();
      filterPopupWindow.setCheckedCircle(selectedCircle);
   }

   private View getCollapseView() {
      Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);

      if (toolbar == null) return null;

      View collapseView = null;
      for (int i = 0; i < toolbar.getChildCount(); i++) {
         View view = toolbar.getChildAt(i);
         if (view instanceof ActionMenuView) {
            collapseView = view;
            break;
         }
      }
      return collapseView;
   }

   private void createSuggestionObserver() {
      contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
         @Override
         public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (!selfChange) {
               contentObserverSubject.onNext(null);
            }
         }
      };
      getContext().getContentResolver()
            .registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, contentObserver);
   }

   private void registerAdditionalCells() {
      fragmentWithFeedDelegate.registerAdditionalCell(SuggestedPhotosCell.SuggestedPhotoModel.class, SuggestedPhotosCell.class);
      fragmentWithFeedDelegate.registerAdditionalCell(EmptyFeedModel.class, EmptyFeedCell.class);
   }

   private void registerCellDelegates() {
      fragmentWithFeedDelegate.registerDelegate(SuggestedPhotosCell.SuggestedPhotoModel.class, this);
      fragmentWithFeedDelegate.registerDelegate(ReloadFeedModel.class, model -> getPresenter().refreshFeed());
      fragmentWithFeedDelegate.registerDelegate(UploadingPostsList.class, new UploadingCellDelegate(getPresenter(),
            getContext()));
      BaseFeedCell.FeedCellDelegate delegate = new FeedCellDelegate(getPresenter());
      fragmentWithFeedDelegate.registerDelegate(PhotoFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(TripFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(BucketFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(PostFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(VideoFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(EmptyFeedModel.class, model -> fragmentWithFeedDelegate.openFriendsSearch());
   }
}
