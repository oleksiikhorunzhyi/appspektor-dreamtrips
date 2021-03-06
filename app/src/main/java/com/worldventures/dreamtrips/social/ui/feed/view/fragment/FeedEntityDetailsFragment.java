package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.fragment.FragmentHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.BucketFeedEntityDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.FeedEntityDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.FeedCellDelegate;

import rx.Observable;

@Layout(R.layout.fragment_comments_with_entity_details)
public class FeedEntityDetailsFragment extends FeedDetailsFragment<FeedEntityDetailsPresenter, FeedEntityDetailsBundle>
      implements FeedEntityDetailsPresenter.View {

   private boolean detailViewAdded = false;

   @Override
   protected FeedEntityDetailsPresenter createPresenter(Bundle savedInstanceState) {
      return new FeedEntityDetailsPresenter(getArgs().getFeedItem(), getArgs().isSlave());
   }

   @Override
   protected void registerCells() {
      adapter.registerCell(BucketFeedItem.class, BucketFeedEntityDetailsCell.class);
      adapter.registerCell(TripFeedItem.class, FeedEntityDetailsCell.class);
      FeedCellDelegate<FeedEntityDetailsPresenter, FeedItem> delegate = new FeedCellDelegate<>(getPresenter());
      delegate.setOnEntityShownInCellAction(getPresenter()::onEntityShownInCell);
      adapter.registerDelegate(BucketFeedItem.class, delegate);
      adapter.registerDelegate(TripFeedItem.class, delegate);
   }

   @Override
   public void onDestroyView() {
      FragmentHelper.resetChildFragmentManagerField(this);
      super.onDestroyView();
   }

   @Override
   public void showDetails(Class<? extends Fragment> fragmentClass, Parcelable extra) {
      if (!detailViewAdded && isVisibleOnScreen()) {
         NavigationConfig config = NavigationConfigBuilder.forFragment()
               .backStackEnabled(false)
               .fragmentManager(getChildFragmentManager())
               .data(extra)
               .containerId(R.id.fragment_details)
               .build();
         router.moveTo(fragmentClass, config);
         detailViewAdded = true;
      }
   }

   @Override
   public void initConnectionOverlay(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper) {
      // Trip details in landscape mode has specific connection overlay parent view down the hierarchy
      if (getArgs().getFeedEntity() instanceof TripModel && ViewUtils.isLandscapeOrientation(getContext())) {
         return;
      }
      super.initConnectionOverlay(connectionStateObservable, stopper);
   }
}
