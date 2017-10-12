package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.AbstractCell;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.bundle.EditPostBundle;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.FeedItemCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.UndefinedFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.uploading.UploadingPostsSectionCell;
import com.worldventures.dreamtrips.social.ui.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.social.ui.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.model.ReloadFeedModel;
import com.worldventures.dreamtrips.social.ui.profile.view.cell.ReloadFeedCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.EditPhotoBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.List;

public class FragmentWithFeedDelegate {

   private Router router;
   private FeedAspectRatioHelper feedAspectRatioHelper;

   private BaseDelegateAdapter adapter;

   public FragmentWithFeedDelegate(Router router, FeedAspectRatioHelper feedAspectRatioHelper) {
      this.router = router;
      this.feedAspectRatioHelper = feedAspectRatioHelper;
   }

   public void init(BaseDelegateAdapter adapter) {
      this.adapter = adapter;
      registerBaseFeedCells();
   }

   public void registerAdditionalCell(Class<?> itemClass, Class<? extends AbstractCell> cellClass) {
      adapter.registerCell(itemClass, cellClass);
   }

   public void registerDelegate(Class<?> itemClass, CellDelegate<?> cellDelegate) {
      adapter.registerDelegate(itemClass, cellDelegate);
   }

   public void addItem(Object item) {
      adapter.addItem(item);
   }

   public void updateItems(final List items, RecyclerView recyclerView) {
      feedAspectRatioHelper.correctAspectRatio(items, (double) recyclerView.getWidth() / recyclerView.getHeight());
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
         @Override
         public int getOldListSize() {
            return adapter.getItems().size();
         }

         @Override
         public int getNewListSize() {
            return items.size();
         }

         @Override
         public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (adapter.getItem(oldItemPosition) instanceof SuggestedPhotosCell.SuggestedPhotoModel
                  && items.get(newItemPosition) instanceof SuggestedPhotosCell.SuggestedPhotoModel) {
               return true;
            }
            if (adapter.getItem(oldItemPosition) instanceof UploadingPostsList
                  && items.get(newItemPosition) instanceof UploadingPostsList) {
               return true;
            }
            return adapter.getItem(oldItemPosition).equals(items.get(newItemPosition));
         }

         @Override
         public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (adapter.getItem(oldItemPosition) instanceof FeedItem
                  && items.get(newItemPosition) instanceof FeedItem) {
               FeedItem oldItem = (FeedItem) adapter.getItem(oldItemPosition);
               FeedItem newItem = (FeedItem) items.get(newItemPosition);
               return oldItem.contentSame(newItem);
            }
            return adapter.getItem(oldItemPosition).equals(items.get(newItemPosition));
         }
      });
      adapter.setItemsNoNotify(items);
      UpdateCallback updateCallback = new UpdateCallback();
      diffResult.dispatchUpdatesTo(updateCallback);
      if (updateCallback.firstInsertPosition == 0) {
         recyclerView.scrollToPosition(updateCallback.firstInsertPosition);
      }
   }

   public List getItems() {
      return adapter.getItems();
   }

   public void notifyDataSetChanged() {
      adapter.notifyDataSetChanged();
   }

   public void notifyItemInserted(int position) {
      adapter.notifyItemInserted(position);
   }

   public void notifyItemChanged(int index) {
      adapter.notifyItemChanged(index);
   }

   public void notifyDataSetChanged(int focusedItemIndex) {
      if (focusedItemIndex != -1) {
         int firstBunchSize = focusedItemIndex;
         int lastBunchSize = adapter.getCount() - focusedItemIndex - 1;

         if (firstBunchSize != 0) adapter.notifyItemRangeChanged(0, firstBunchSize);
         adapter.notifyItemChanged(focusedItemIndex);
         if (lastBunchSize != 0) adapter.notifyItemRangeChanged(focusedItemIndex + 1, lastBunchSize);
      } else {
         notifyDataSetChanged();
      }
   }

   public void notifyItemChanged(FeedItem feedItem) {
      if (feedItem == null) return;
      int size = adapter.getItems().size();
      for (int i = 0; i < size; i++) {
         Object object = adapter.getItems().get(i);
         if (object instanceof FeedItem && ((FeedItem) object).equalsWith(feedItem)) {
            adapter.notifyItemChanged(i);
            return;
         }
      }
   }

   /**
    * After leaving feed items list screen all translated items should reset view state to original
    * (translation hides and 'Translation' button is visible)
    */
   public void resetTranslatedStatus() {
      Queryable.from(adapter.getItems()).forEachR(item -> {
         if (item instanceof FeedItem) ((FeedItem) item).getItem().setTranslated(false);
      });
      notifyDataSetChanged();
   }

   private void registerBaseFeedCells() {
      adapter.registerCell(ReloadFeedModel.class, ReloadFeedCell.class);
      adapter.registerCell(PhotoFeedItem.class, FeedItemCell.class);
      adapter.registerCell(TripFeedItem.class, FeedItemCell.class);
      adapter.registerCell(BucketFeedItem.class, FeedItemCell.class);
      adapter.registerCell(PostFeedItem.class, FeedItemCell.class);
      adapter.registerCell(VideoFeedItem.class, FeedItemCell.class);
      adapter.registerCell(UndefinedFeedItem.class, UndefinedFeedItemDetailsCell.class);
      adapter.registerCell(UploadingPostsList.class, UploadingPostsSectionCell.class);
      adapter.registerCell(LoadMoreModel.class, LoaderCell.class);
   }

   /////////////////////////////////////////
   //  Routes
   ////////////////////////////////////////

   public void openAccountProfile(User account) {
      router.moveTo(Route.ACCOUNT_PROFILE, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(new UserBundle(account))
            .build());
   }

   public void openFriends(FriendMainBundle bundle) {
      router.moveTo(Route.FRIENDS, NavigationConfigBuilder.forActivity().data(bundle).build());
   }

   public void openTextualPostEdit(FragmentManager fragmentManager, TextualPost textualPost) {
      @IdRes int containerId = R.id.container_details_floating;
      router.moveTo(Route.EDIT_POST, NavigationConfigBuilder.forRemoval()
            .containerId(containerId)
            .fragmentManager(fragmentManager)
            .build());
      router.moveTo(Route.EDIT_POST, NavigationConfigBuilder.forFragment()
            .containerId(containerId)
            .backStackEnabled(false)
            .fragmentManager(fragmentManager)
            .data(new EditPostBundle(textualPost))
            .build());
   }

   public void openPhotoEdit(FragmentManager fragmentManager, Photo photo) {
      @IdRes int containerId = R.id.container_details_floating;
      router.moveTo(Route.EDIT_PHOTO, NavigationConfigBuilder.forRemoval()
            .containerId(containerId)
            .fragmentManager(fragmentManager)
            .build());
      router.moveTo(Route.EDIT_PHOTO, NavigationConfigBuilder.forFragment()
            .containerId(containerId)
            .backStackEnabled(false)
            .fragmentManager(fragmentManager)
            .data(new EditPhotoBundle(photo))
            .build());
   }

   public void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle) {
      router.moveTo(route, NavigationConfigBuilder.forActivity().data(foreignBucketBundle).build());
   }

   public void openBucketEdit(FragmentManager fragmentManager, boolean isTabletLandscape, BucketBundle bucketBundle) {
      @IdRes int containerId = R.id.container_details_floating;
      bucketBundle.setLock(true);
      if (isTabletLandscape) {
         router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forFragment()
               .backStackEnabled(true)
               .containerId(containerId)
               .fragmentManager(fragmentManager)
               .data(bucketBundle)
               .build());
      } else {
         router.moveTo(Route.BUCKET_EDIT, NavigationConfigBuilder.forActivity().data(bucketBundle).build());
      }
   }

   public void openTripImages(Route route, TripImagesArgs tripImagesBundle) {
      router.moveTo(route, NavigationConfigBuilder.forActivity().data(tripImagesBundle).build());
   }

   public void openComments(FeedItem feedItem, boolean isVisible, boolean isTabletLandscape) {
      if (isVisible) {
         Route detailsRoute = Route.FEED_ITEM_DETAILS;
         FeedItemDetailsBundle.Builder bundleBuilder = new FeedItemDetailsBundle.Builder().feedItem(feedItem)
               .showAdditionalInfo(true)
               .openKeyboard(true);
         if (isTabletLandscape) {
            bundleBuilder.slave(true);
         }
         router.moveTo(detailsRoute, NavigationConfigBuilder.forActivity().manualOrientationActivity(true)
               .data(bundleBuilder.build()).build());
      }
   }

   public void openFriendsSearch() {
      router.moveTo(Route.FRIEND_SEARCH, NavigationConfigBuilder.forActivity()
            .data(new FriendGlobalSearchBundle(""))
            .build());
   }

   public void openPost(FragmentManager fragmentManager) {
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
            .containerId(R.id.container_details_floating)
            .fragmentManager(fragmentManager)
            .build());
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(fragmentManager)
            .data(new CreateEntityBundle(false, CreateEntityBundle.Origin.FEED))
            .containerId(R.id.container_details_floating)
            .build());
   }

   public void openSharePhoto(FragmentManager fragmentManager, CreateEntityBundle bundle) {
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
            .containerId(R.id.container_details_floating)
            .fragmentManager(fragmentManager)
            .build());
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(fragmentManager)
            .containerId(R.id.container_details_floating)
            .data(bundle)
            .build());
   }

   public void openFeedAdditionalInfo(FragmentManager fragmentManager, User account) {
      router.moveTo(Route.FEED_LIST_ADDITIONAL_INFO, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(fragmentManager)
            .containerId(R.id.additional_info_container)
            .data(new FeedAdditionalInfoBundle(account))
            .build());
   }

   public void hideAdditonalInfo(FragmentManager fragmentManager) {
      router.moveTo(Route.FEED_LIST_ADDITIONAL_INFO, NavigationConfigBuilder.forRemoval()
            .fragmentManager(fragmentManager)
            .containerId(R.id.additional_info_container)
            .build());
   }

   public void openHashtagSearch() {
      router.moveTo(Route.FEED_HASHTAG, NavigationConfigBuilder.forActivity()
            .data(null)
            .manualOrientationActivity(true)
            .toolbarConfig(ToolbarConfig.Builder.create().visible(true).build())
            .build());
   }

   /*
    * Copy of default DiffUtil implementation with ability
    * to track and save insert position
    */
   private class UpdateCallback implements ListUpdateCallback {
      private int firstInsertPosition = -1;

      @Override
      public void onInserted(int position, int count) {
         if (firstInsertPosition < position) {
            firstInsertPosition = position;
         }
         adapter.notifyItemRangeInserted(position, count);
      }

      @Override
      public void onRemoved(int position, int count) {
         adapter.notifyItemRangeRemoved(position, count);
      }

      @Override
      public void onMoved(int fromPosition, int toPosition) {
         adapter.notifyItemMoved(fromPosition, toPosition);
      }

      @Override
      public void onChanged(int position, int count, Object payload) {
         adapter.notifyItemRangeChanged(position, count, payload);
      }
   }
}
