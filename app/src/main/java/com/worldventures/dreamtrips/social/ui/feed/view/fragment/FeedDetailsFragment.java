package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.content.res.Configuration;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.model.User;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.fragment.FragmentHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.social.ui.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import icepick.State;
import timber.log.Timber;

public abstract class FeedDetailsFragment<PRESENTER extends FeedDetailsPresenter, P extends FeedDetailsBundle>
      extends CommentableFragment<PRESENTER, P> implements FeedDetailsPresenter.View {

   private static final int INPUT_PANEL_SHOW_OFFSET = 20;

   @Optional @InjectView(R.id.comments_additional_info_container) ViewGroup additionalContainer;

   private int loadMoreOffset;

   @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;
   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;

   @State FeedItem feedItem;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      registerCells();

      recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            updateStickyInputContainerState();
         }
      });
      recyclerView.setAdapterDataCallback(new RecyclerView.AdapterDataObserver() {
         @Override
         public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            updateStickyInputContainerState();
         }
      });

      showAdditionalContainerIfNeeded();

      recyclerView.post(() -> {
         if (recyclerView != null) {
            recyclerView.scrollBy(0, 1);
         }
      });
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      showAdditionalInfoIfNeeded();
      updateStickyInputContainerState();
   }

   private void updateStickyInputContainerState() {
      View view = layout.findViewByPosition(0);
      if (layout.findFirstVisibleItemPosition() > 0 || isNeedToShowInputPanel(view)) {
         inputContainer.setVisibility(View.VISIBLE);
      } else {
         inputContainer.setVisibility(View.GONE);
      }
   }

   private boolean isNeedToShowInputPanel(View view) {
      if (view == null) {
         return false;
      }
      //
      int[] location = new int[2];
      view.getLocationOnScreen(location);

      return location[1] + view.getHeight() <= ViewUtils.getScreenHeight(getActivity()) + INPUT_PANEL_SHOW_OFFSET;
   }

   @Override
   public void onResume() {
      super.onResume();
      activeFeedRouteInteractor.activeFeedRouteCommandActionPipe()
            .send(ActiveFeedRouteCommand.update(FeedCellListWidthProvider.FeedType.FEED_DETAILS));
   }

   @Override
   public void onDestroyView() {
      FragmentHelper.resetChildFragmentManagerField(this);
      //
      super.onDestroyView();
   }

   @Override
   public void setFeedItem(FeedItem feedItem) {
      adapter.addItem(0, feedItem);
      adapter.notifyItemInserted(0);
      loadMoreOffset = 1;
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
      @IdRes int containerId = R.id.container_details_floating;
      BucketBundle bucketBundle = new BucketBundle(bucketItem, type);
      bucketBundle.setLock(true);
      try {
         bucketBundle.setOwnerId(getArgs().getFeedEntity().getOwner().getId());
      } catch (Exception e) {
         Timber.e(e, "");
      }
      if (isTabletLandscape()) {
         router.moveTo(BucketItemEditFragment.class, NavigationConfigBuilder.forFragment()
               .backStackEnabled(true)
               .containerId(containerId)
               .fragmentManager(getActivity().getSupportFragmentManager())
               .data(bucketBundle)
               .build());
         showContainer(containerId);
      } else {
         router.moveTo(BucketItemEditFragment.class, NavigationConfigBuilder.forActivity().data(bucketBundle).build());
      }
   }

   private void showContainer(@IdRes int containerId) {
      View container = ButterKnife.findById(getActivity(), containerId);
      if (container != null) {
         container.setVisibility(View.VISIBLE);
      }
   }

   private void showAdditionalInfoIfNeeded() {
      User user = feedItem.getItem().getOwner();
      showAdditionalContainerIfNeeded();
      if (!isAdditionalInfoFragmentAttached() && isShowAdditionalInfo()) {
         router.moveTo(FeedItemAdditionalInfoFragment.class, NavigationConfigBuilder.forFragment()
               .backStackEnabled(false)
               .fragmentManager(getChildFragmentManager())
               .containerId(R.id.comments_additional_info_container)
               .data(new FeedAdditionalInfoBundle(user))
               .build());
      }
   }

   private void showAdditionalContainerIfNeeded() {
      if (additionalContainer == null) {
         return;
      }
      if (isShowAdditionalInfo()) {
         additionalContainer.setVisibility(View.VISIBLE);
      } else {
         additionalContainer.setVisibility(View.GONE);
      }
   }

   protected boolean isShowAdditionalInfo() {
      return getArgs().shouldShowAdditionalInfo() && !getPresenter().isTrip() && isTabletLandscape();
   }

   private boolean isAdditionalInfoFragmentAttached() {
      return getActivity().getSupportFragmentManager()
            .findFragmentById(R.id.comments_additional_info_container) != null;
   }

   @Override
   public void updateFeedItem(FeedItem feedItem) {
      this.feedItem = feedItem;
      if (feedItem != null) {
         adapter.updateItem(feedItem);
         showAdditionalInfoIfNeeded();
      } else {
         adapter.notifyDataSetChanged(); //there has been error. Cells need to be resynced
      }
   }

   @Override
   protected int getAdditionalItemsCount() {
      return super.getAdditionalItemsCount() + loadMoreOffset;
   }

   @Override
   protected int getLoadMorePosition() {
      return super.getLoadMorePosition() + loadMoreOffset;
   }

   protected abstract void registerCells();
}
