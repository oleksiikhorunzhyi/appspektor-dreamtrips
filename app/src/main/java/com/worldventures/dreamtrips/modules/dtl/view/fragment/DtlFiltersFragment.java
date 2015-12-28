package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.appyvet.rangebar.RangeBar;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.util.DrawerListener;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsFilterAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsFilterHeaderAttribute;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlFiltersPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

import java.util.Collections;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_filters)
public class DtlFiltersFragment extends RxBaseFragment<DtlFiltersPresenter>
        implements DtlFiltersPresenter.View, DrawerListener {

    @InjectView(R.id.range_bar_distance)
    protected RangeBar rangeBarDistance;
    @InjectView(R.id.range_bar_price)
    protected RangeBar rangeBarPrice;
    @InjectView(R.id.switchView)
    protected SwitchCompat switchCompat;
    @InjectView(R.id.recyclerViewFilters)
    protected RecyclerView recyclerViewFilters;

    protected BaseArrayListAdapter<DtlMerchantsFilterAttribute> filtersAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerViewFilters.setLayoutManager(layoutManager);

        this.filtersAdapter = new BaseArrayListAdapter<>(getActivity(), this);
        this.filtersAdapter.registerCell(DtlMerchantsFilterHeaderAttribute.class, DtlFilterAttributeHeaderCell.class);
        this.filtersAdapter.registerCell(DtlMerchantsFilterAttribute.class, DtlFilterAttributeCell.class);

        recyclerViewFilters.setAdapter(filtersAdapter);

        rangeBarDistance.setOnRangeBarChangeListener((rangeBar, leftIndex, rightIndex, leftValue, rightValue) ->
                getPresenter().distanceChanged(Integer.valueOf(rightValue)));
        rangeBarPrice.setOnRangeBarChangeListener((rangeBar, leftIndex, rightIndex, leftValue, rightValue) ->
                getPresenter().priceChanged(Integer.valueOf(leftValue), Integer.valueOf(rightValue)));
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> getPresenter().toggleDistance(isChecked));
        ((MainActivity) getActivity()).attachRightDrawerListener(this);
    }

    @OnClick(R.id.apply)
    void onApply() {
        //TODO Think about drawers without dependency of MainActivity
        ((MainActivity) getActivity()).closeRightDrawer();
        getPresenter().apply();
    }

    @OnClick(R.id.reset)
    void onReset() {
        //TODO Think about drawers without dependency of MainActivity
        ((MainActivity) getActivity()).closeRightDrawer();
        getPresenter().resetAll();
    }

    @Override
    protected DtlFiltersPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlFiltersPresenter();
    }

    @Override
    public void attachFilterData(DtlFilterData filterData) {
        rangeBarDistance.setRangePinsByValue(10.0f, filterData.getMaxDistance());
        rangeBarPrice.setRangePinsByValue(filterData.getMinPrice(), filterData.getMaxPrice());
        switchCompat.setChecked(filterData.getDistanceType() == DtlFilterData.DistanceType.KMS);
        if (filterData.getAmenities() != null && !filterData.getAmenities().isEmpty())
            setupAttributesHeader(filterData);
    }

    @Override
    public void dataSetChanged() {
        filtersAdapter.notifyDataSetChanged();
    }

    private void setupAttributesHeader(DtlFilterData filterData) {
        Collections.sort(filterData.getAmenities());
        filtersAdapter.clearAndUpdateItems(filterData.getAmenities());
        filtersAdapter.addItem(0, new DtlMerchantsFilterHeaderAttribute(getString(R.string.dtl_amenities)));
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) getActivity()).detachRightDrawerListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDrawerOpened() {
    }

    @Override
    public void onDrawerClosed() {
        // do nothing?
    }
}
