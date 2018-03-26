package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapListPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripMapListBundle;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripMapCell;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityDetailsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_trip_map_list)
public class TripMapListFragment extends BaseFragmentWithArgs<TripMapListPresenter, TripMapListBundle> implements TripMapListPresenter.View, CellDelegate<TripModel> {

   @InjectView(R.id.trip_list) RecyclerView tripView;

   private BaseDelegateAdapter adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter = new BaseDelegateAdapter(getContext(), this);

      adapter.registerCell(TripModel.class, TripMapCell.class);
      adapter.registerDelegate(TripModel.class, this);

      tripView.setLayoutManager(new LinearLayoutManager(getContext()));
      tripView.setAdapter(adapter);
   }

   @Override
   protected TripMapListPresenter createPresenter(Bundle savedInstanceState) {
      return new TripMapListPresenter(getArgs().getTrips());
   }

   @Override
   public void updateItems(@NotNull List<? extends TripModel> trips) {
      adapter.setItems(trips);
   }

   @Override
   public void onCellClicked(TripModel model) {
      router.moveTo(FeedEntityDetailsFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create()
                  .visible(false)
                  .build())
            .data(new FeedEntityDetailsBundle.Builder().feedItem(FeedItem.create(model, null))
                  .showAdditionalInfo(true)
                  .build())
            .build());
   }
}
