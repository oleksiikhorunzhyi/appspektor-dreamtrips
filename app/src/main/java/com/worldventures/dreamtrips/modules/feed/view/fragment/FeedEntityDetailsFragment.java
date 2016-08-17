package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.FragmentHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedEntityDetailsCell;

@Layout(R.layout.fragment_comments_with_entity_details)
public class FeedEntityDetailsFragment extends FeedDetailsFragment<FeedEntityDetailsPresenter, FeedEntityDetailsBundle> implements FeedEntityDetailsPresenter.View {

   @Override
   protected FeedEntityDetailsPresenter createPresenter(Bundle savedInstanceState) {
      return new FeedEntityDetailsPresenter(getArgs().getFeedItem(), getArgs().isSlave());
   }

   @Override
   protected void registerCells() {
      adapter.registerCell(BucketFeedItem.class, BucketFeedEntityDetailsCell.class);
      adapter.registerCell(TripFeedItem.class, FeedEntityDetailsCell.class);
   }

   @Override
   public void onDestroyView() {
      FragmentHelper.resetChildFragmentManagerField(this);
      //
      super.onDestroyView();
   }

   @Override
   public void showDetails(Route route, Parcelable extra) {
      Fragment entityFragment = getChildFragmentManager().findFragmentById(R.id.fragment_details);
      boolean notAdded = entityFragment == null || entityFragment.getView() == null || entityFragment.getView()
            .getParent() == null || !entityFragment.getClass().getName().equals(route.getClazzName());
      if (notAdded) {
         NavigationConfig config = NavigationConfigBuilder.forFragment()
               .backStackEnabled(false)
               .fragmentManager(getChildFragmentManager())
               .data(extra)
               .containerId(R.id.fragment_details)
               .build();
         router.moveTo(route, config);
      }
   }
}
