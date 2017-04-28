package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.ui.fragment.FragmentHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

public abstract class FeedDetailsFragment<PRESENTER extends FeedDetailsPresenter, P extends FeedDetailsBundle>
      extends CommentableFragment<PRESENTER, P> implements FeedDetailsPresenter.View {

   private static final int INPUT_PANEL_SHOW_OFFSET = 20;

   @Optional @InjectView(R.id.comments_additional_info_container) ViewGroup additionalContainer;

   private int loadMoreOffset;

   @Inject FragmentWithFeedDelegate fragmentWithFeedDelegate;

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
      if (!isShowAdditionalInfo() && additionalContainer != null) {
         additionalContainer.setVisibility(View.GONE);
      }
      recyclerView.post(() -> {
         if (recyclerView != null) recyclerView.scrollBy(0, 1);
      });
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
      if (view == null) return false;
      //
      int[] location = new int[2];
      view.getLocationOnScreen(location);

      return location[1] + view.getHeight() <= ViewUtils.getScreenHeight(getActivity()) + INPUT_PANEL_SHOW_OFFSET;
   }

   protected abstract void registerCells();

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
         router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forFragment()
               .backStackEnabled(true)
               .containerId(containerId)
               .fragmentManager(getActivity().getSupportFragmentManager())
               .data(bucketBundle)
               .build());
         showContainer(containerId);
      } else {
         router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forActivity().data(bucketBundle).build());
      }
   }

   private void showContainer(@IdRes int containerId) {
      View container = ButterKnife.findById(getActivity(), containerId);
      if (container != null) container.setVisibility(View.VISIBLE);
   }

   @Override
   public void showAdditionalInfo(User user) {
      if (isAdditionalContainerEmpty() && isShowAdditionalInfo()) {
         router.moveTo(Route.FEED_ITEM_ADDITIONAL_INFO, NavigationConfigBuilder.forFragment()
               .backStackEnabled(false)
               .fragmentManager(getChildFragmentManager())
               .containerId(R.id.comments_additional_info_container)
               .data(new FeedAdditionalInfoBundle(user))
               .build());
      }
   }

   private boolean isShowAdditionalInfo() {
      return getArgs().shouldShowAdditionalInfo() && !getPresenter().isTrip() && isTabletLandscape();
   }

   private boolean isAdditionalContainerEmpty() {
      return getActivity().getSupportFragmentManager()
            .findFragmentById(R.id.comments_additional_info_container) == null;
   }

   @Override
   public void updateFeedItem(FeedItem feedItem) {
      if (feedItem != null) {
         adapter.updateItem(feedItem);
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
}
