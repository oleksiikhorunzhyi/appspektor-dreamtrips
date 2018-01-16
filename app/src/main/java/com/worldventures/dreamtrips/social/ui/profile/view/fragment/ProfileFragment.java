package com.worldventures.dreamtrips.social.ui.profile.view.fragment;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.model.User;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.FeedCellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.SideMarginsItemDecorator;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FocusableStatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.model.ReloadFeedModel;
import com.worldventures.dreamtrips.social.ui.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.social.ui.profile.view.ProfileViewUtils;
import com.worldventures.dreamtrips.social.ui.profile.view.cell.ProfileCell;
import com.worldventures.dreamtrips.social.ui.profile.view.cell.delegate.ProfileCellDelegate;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public abstract class ProfileFragment<T extends ProfilePresenter> extends RxBaseFragmentWithArgs<T, UserBundle>
      implements ProfilePresenter.View, SwipeRefreshLayout.OnRefreshListener, ProfileCellDelegate,
      FeedEntityEditingView {

   @InjectView(R.id.profile_toolbar) Toolbar profileToolbar;
   @InjectView(R.id.profile_toolbar_title) TextView profileToolbarTitle;
   @InjectView(R.id.profile_user_status) TextView profileToolbarUserStatus;

   @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;
   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;

   private int scrollArea;

   private Bundle savedInstanceState;

   protected FocusableStatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
      calculateScrollArea();
   }

   @Override
   public void onResume() {
      super.onResume();
      if (profileToolbar != null) {
         float percent = calculateOffset();
         setToolbarAlpha(percent);
      }
      startAutoplayVideos();
      activeFeedRouteInteractor.activeFeedRouteCommandActionPipe()
            .send(ActiveFeedRouteCommand.update(FeedCellListWidthProvider.FeedType.TIMELINE));
   }

   protected void startAutoplayVideos() {
      statePaginatedRecyclerViewManager.startLookingForCompletelyVisibleItem(bindUntilPauseComposer());
   }

   @Override
   public void onPause() {
      super.onPause();
      statePaginatedRecyclerViewManager.stopAutoplayVideos();
      setToolbarAlpha(100);
   }

   @Override
   public void onStop() {
      super.onStop();
      fragmentWithFeedDelegate.resetTranslatedStatus();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      BaseDelegateAdapter adapter = new BaseDelegateAdapter(getContext(), this);
      statePaginatedRecyclerViewManager = new FocusableStatePaginatedRecyclerViewManager(rootView.findViewById(R.id.recyclerView),
            rootView.findViewById(R.id.swipe_container));
      statePaginatedRecyclerViewManager.init(adapter, savedInstanceState);
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.setPaginationListener(() -> {
         if (!statePaginatedRecyclerViewManager.isNoMoreElements() && getPresenter().onLoadNext()) {
            fragmentWithFeedDelegate.addItem(new LoadMoreModel());
            fragmentWithFeedDelegate.notifyItemInserted(fragmentWithFeedDelegate.getItems().size() - 1);
         }
      });
      if (ViewUtils.isTablet(getContext())) {
         int margin = getResources().getInteger(R.integer.feed_landscape_horizontal_margin);
         statePaginatedRecyclerViewManager.addItemDecoration(new SideMarginsItemDecorator(margin, true));
      }
      statePaginatedRecyclerViewManager.setOffsetYListener(yOffset -> {
         float percent = calculateOffset();
         setToolbarAlpha(percent);
         if (percent >= 1.0) {
            profileToolbarTitle.setVisibility(View.VISIBLE);
            profileToolbarUserStatus.setVisibility(View.VISIBLE);
         } else {
            profileToolbarTitle.setVisibility(View.INVISIBLE);
            profileToolbarUserStatus.setVisibility(View.INVISIBLE);
         }
      });

      fragmentWithFeedDelegate.init(adapter);
      registerAdditionalCells();
      registerCellDelegates();
      initToolbar();
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      initToolbar();
   }

   @Override
   public void dataSetChanged() {
      fragmentWithFeedDelegate.notifyDataSetChanged(statePaginatedRecyclerViewManager.findFocusedPosition());
   }

   @Override
   public void updateItem(FeedItem feedItem) {
      fragmentWithFeedDelegate.notifyItemChanged(feedItem);
   }

   @Override
   public void openFriends() {
      fragmentWithFeedDelegate.openFriends(null);
   }

   @Override
   public void openBucketList(Class<? extends BucketTabsFragment> clazz, ForeignBucketTabsBundle foreignBucketBundle) {
      fragmentWithFeedDelegate.openBucketList(clazz, foreignBucketBundle);
   }

   @Override
   public void openTripImages(TripImagesArgs tripImagesBundle) {
      router.moveTo(TripImagesFragment.class, NavigationConfigBuilder.forActivity().data(tripImagesBundle).build());
   }

   @Override
   public void openPost() {
      fragmentWithFeedDelegate.openPost(getActivity().getSupportFragmentManager());
   }

   @Override
   public void refreshFeedItems(List<FeedItem> items, User user) {
      List feedModels = new ArrayList();
      feedModels.add(user);
      feedModels.addAll(items);
      fragmentWithFeedDelegate.updateItems(feedModels, statePaginatedRecyclerViewManager.getStateRecyclerView());
      ProfileViewUtils.setUserStatus(user, profileToolbarUserStatus, getResources());
      profileToolbarTitle.setText(user.getFullName());
   }

   @Override
   public void onRefresh() {
      getPresenter().onRefresh();
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
   public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
      statePaginatedRecyclerViewManager.updateLoadingStatus(loading, noMoreElements);
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
   public void openComments(FeedItem feedItem) {
      fragmentWithFeedDelegate.openComments(feedItem, isVisibleOnScreen(), isTabletLandscape());
   }

   protected abstract void initToolbar();

   private float calculateOffset() {
      return Math.min(statePaginatedRecyclerViewManager.getStateRecyclerView()
            .getScrollOffset() / (float) scrollArea, 1);
   }

   private void setToolbarAlpha(float percentage) {
      Drawable c = profileToolbar.getBackground();
      int round = Math.round(Math.min(1, percentage * 2) * 255);
      c.setAlpha(round);
      profileToolbar.setBackgroundDrawable(c);
   }

   private void calculateScrollArea() {
      TypedValue tv = new TypedValue();
      int actionBarHeight = 0;
      if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
         actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
      }
      int profilePhotoHeight = getResources().getDimensionPixelSize(R.dimen.profile_cover_height);
      scrollArea = profilePhotoHeight - actionBarHeight;
   }

   private void registerAdditionalCells() {
      fragmentWithFeedDelegate.registerAdditionalCell(User.class, ProfileCell.class);
   }

   protected void registerCellDelegates() {
      fragmentWithFeedDelegate.registerDelegate(User.class, this);
      fragmentWithFeedDelegate.registerDelegate(ReloadFeedModel.class, model -> getPresenter().onRefresh());

      BaseFeedCell.FeedCellDelegate delegate = new FeedCellDelegate(getPresenter());
      fragmentWithFeedDelegate.registerDelegate(PhotoFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(TripFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(BucketFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(PostFeedItem.class, delegate);
      fragmentWithFeedDelegate.registerDelegate(VideoFeedItem.class, delegate);
   }

   @Override
   public void notifyDataSetChanged() {
      fragmentWithFeedDelegate.notifyDataSetChanged();
   }

   @Override
   public void flagSentSuccess() {
      informUser(R.string.flag_sent_success_msg);
   }

   @Override
   public void onBucketListClicked() {
      getPresenter().openBucketList();
   }

   @Override
   public void onTripImagesClicked() {
      getPresenter().openTripImages();
   }

   @Override
   public void onFriendsClicked() {
      getPresenter().openFriends();
   }

   @Override
   public void onCreatePostClicked() {
      getPresenter().makePost();
   }

   @Override
   public void onUserPhotoClicked() {}

   @Override
   public void onUserCoverClicked() {}

   @Override
   public void onAcceptRequest() {}

   @Override
   public void onRejectRequest() {}

   @Override
   public void onAddFriend() { }

   @Override
   public void onCellClicked(User model) { }
}
