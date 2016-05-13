package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapListPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripMapListBundle;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripMapCell;

import java.util.List;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_trip_map_list)
public class TripMapListFragment extends BaseFragmentWithArgs<TripMapListPresenter, TripMapListBundle>
        implements TripMapListPresenter.View, CellDelegate<TripModel> {

    @InjectView(R.id.trip_list)
    RecyclerView tripView;
    @Optional
    @InjectView(R.id.left_pointer)
    View leftPointer;
    @Optional
    @InjectView(R.id.right_pointer)
    View rightPointer;
    @Optional
    @InjectView(R.id.bottom_pointer)
    View bottomPointer;
    @Optional
    @InjectView(R.id.left_space)
    View leftSpace;
    @Optional
    @InjectView(R.id.right_space)
    View rightSpace;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArgs().getAnchor() != null) {
            switch (getArgs().getAnchor().getPointerPosition()) {
                case BOTTOM:
                    bottomPointer.setVisibility(View.VISIBLE);
                    break;
                case LEFT:
                    leftPointer.setVisibility(View.VISIBLE);
                    leftSpace.getLayoutParams().height = getArgs().getAnchor().getMargin();
                    break;
                case RIGHT:
                    rightPointer.setVisibility(View.VISIBLE);
                    rightSpace.getLayoutParams().height = getArgs().getAnchor().getMargin();
                    break;
            }
        }
    }

    @Override
    protected TripMapListPresenter createPresenter(Bundle savedInstanceState) {
        return new TripMapListPresenter(getArgs().getTrips());
    }

    @Override
    public void updateItems(List<TripModel> trips) {
        adapter.setItems(trips);
    }

    @Override
    public void onCellClicked(TripModel model) {
        router.moveTo(Route.FEED_ENTITY_DETAILS, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new FeedDetailsBundle(FeedItem.create(model, null)))
                .build());
    }
}
