package com.worldventures.dreamtrips.modules.feed.view.fragment;

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
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeImageView;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedBundle;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.cell.EmptyFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.modules.feed.view.cell.EmptyFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.FeedCellDelegate;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.SuggestedPhotosDelegate;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.UploadingCellDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.model.ReloadFeedModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import rx.Observable;
import rx.subjects.PublishSubject;

@Layout(R.layout.fragment_feed)
@MenuResource(R.menu.menu_activity_feed)
public class FeedFragment extends RxBaseFragmentWithArgs<FeedPresenter, FeedBundle> implements FeedPresenter.View,
      SwipeRefreshLayout.OnRefreshListener, SuggestedPhotosDelegate, SuggestedPhotoCellPresenterHelper.OutViewBinder,
      FeedEntityEditingView {

   @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;

   @Optional
   @InjectView(R.id.posting_header) View postingHeader;

   private BadgeImageView friendsBadge;
   private BadgeImageView unreadConversationBadge;

   private CirclesFilterPopupWindow filterPopupWindow;

   private ContentObserver contentObserver;
   private PublishSubject<Void> contentObserverSubject = PublishSubject.create();

   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private Bundle savedInstanceState;

   private MaterialDialog blockingProgressDialog;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
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
      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
      statePaginatedRecyclerViewManager.init(adapter, savedInstanceState);
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.setPaginationListener(() -> {
         if (!statePaginatedRecyclerViewManager.isNoMoreElements() && getPresenter().loadNext()) {
            fragmentWithFeedDelegate.addItem(new LoadMoreModel());
            fragmentWithFeedDelegate.notifyDataSetChanged();
         }
      });
      if (isTabletLandscape()) {
         fragmentWithFeedDelegate.openFeedAdditionalInfo(getChildFragmentManager(), getPresenter().getAccount());
      }

      fragmentWithFeedDelegate.init(adapter);
      registerAdditionalCells();
      registerCellDelegates();
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      MenuItem friendsItem = menu.findItem(R.id.action_friend_requests);
      friendsBadge = (BadgeImageView) MenuItemCompat.getActionView(friendsItem);
      setRequestsCount(getPresenter().getFriendsRequestsCount());
      friendsBadge.setOnClickListener(v -> {
         fragmentWithFeedDelegate.openFriends(new FriendMainBundle(FriendMainBundle.REQUESTS));
         TrackingHelper.tapFeedButton(TrackingHelper.ATTRIBUTE_OPEN_FRIENDS);
      });

      MenuItem conversationItem = menu.findItem(R.id.action_unread_conversation);
      if (conversationItem != null) {
         unreadConversationBadge = (BadgeImageView) MenuItemCompat.getActionView(conversationItem);
         unreadConversationBadge.setImage(R.drawable.messenger_icon_white);
         unreadConversationBadge.setBadgeValue(getPresenter().getUnreadConversationCount());
         unreadConversationBadge.setOnClickListener(v -> getPresenter().onUnreadConversationsClick());
      }
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

   @Optional
   @OnClick(R.id.share_photo)
   protected void onSharePhotoClick() {
      fragmentWithFeedDelegate.openSharePhoto(getActivity().getSupportFragmentManager(),
            new CreateEntityBundle(true, CreateEntityBundle.Origin.FEED));
   }

   @Override
   public void onAttachClicked() {
      fragmentWithFeedDelegate.openSharePhoto(getActivity().getSupportFragmentManager(), new CreateEntityBundle(false,
            CreateEntityBundle.Origin.FEED));
      getPresenter().attachSelectedSuggestionPhotos();
      getPresenter().removeSuggestedPhotos();
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
      getPresenter().takeSuggestionView(view, this, savedInstanceState, contentObserverSubject.asObservable()
            .throttleLast(1, TimeUnit.SECONDS));
   }

   @Override
   public void onSyncViewState() {
      getPresenter().syncSuggestionViewState();
   }

   @Override
   public void onPreloadSuggestionPhotos(@NonNull PhotoGalleryModel model) {
      getPresenter().preloadSuggestionChunk(model);
   }

   @Override
   public void onSelectPhoto(@NonNull PhotoGalleryModel model) {
      getPresenter().selectPhoto(model);
   }

   @Override
   public long lastSyncTimestamp() {
      return getPresenter().lastSyncTimestamp();
   }

   @Override
   public void onCellClicked(MediaAttachment model) {
      // nothing to do
   }

   @Override
   public <T> Observable<T> bindOutLifecycle(Observable<T> observable) {
      return bind(observable);
   }

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
   public void refreshFeedItems(List<FeedItem> feedItems, UploadingPostsList uploadingPostsList,
         List<PhotoGalleryModel> suggestedPhotos) {
      List feedModels = new ArrayList();
      processSuggestedPhotosItems(suggestedPhotos, feedModels);
      processUploadsInProgressItems(uploadingPostsList, feedModels);
      processFeedItems(feedItems, feedModels);
      fragmentWithFeedDelegate.clearItems();
      fragmentWithFeedDelegate.addItems(feedModels);
      fragmentWithFeedDelegate.notifyDataSetChanged();
   }

   private void processSuggestedPhotosItems(List<PhotoGalleryModel> suggestedPhotos, List feedModels) {
      int suggestedPhotosSize = suggestedPhotos == null ? 0 : suggestedPhotos.size();
      if (suggestedPhotosSize > 0) {
         feedModels.add(new MediaAttachment(suggestedPhotos, MediaAttachment.Source.GALLERY));
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
      statePaginatedRecyclerViewManager.startLoading();
   }

   @Override
   public void finishLoading() {
      statePaginatedRecyclerViewManager.finishLoading();
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
   public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
      statePaginatedRecyclerViewManager.updateLoadingStatus(loading, noMoreElements);
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
      fragmentWithFeedDelegate.registerAdditionalCell(MediaAttachment.class, SuggestedPhotosCell.class);
      fragmentWithFeedDelegate.registerAdditionalCell(EmptyFeedModel.class, EmptyFeedCell.class);
   }

   private void registerCellDelegates() {
      fragmentWithFeedDelegate.registerDelegate(MediaAttachment.class, this);
      fragmentWithFeedDelegate.registerDelegate(ReloadFeedModel.class, model -> getPresenter().refreshFeed());
      fragmentWithFeedDelegate.registerDelegate(UploadingPostsList.class, new UploadingCellDelegate(getPresenter(),
            getContext()));
      BaseFeedCell.FeedCellDelegate delegate = new FeedCellDelegate(getPresenter());
      fragmentWithFeedDelegate.registerDelegate(PhotoFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(TripFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(BucketFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(PostFeedItem.class, delegate);

      fragmentWithFeedDelegate.registerDelegate(EmptyFeedModel.class,
            model -> fragmentWithFeedDelegate.openFriendsSearch());
   }
}
