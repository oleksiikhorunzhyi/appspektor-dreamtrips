package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.model.DtlAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlFiltersPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_filters)
public class DtlFiltersFragment extends BaseFragment<DtlFiltersPresenter> implements DtlFiltersPresenter.View {

    @InjectView(R.id.range_bar_distance)
    protected RangeBar rangeBarDistance;
    @InjectView(R.id.range_bar_price)
    protected RangeBar rangeBarPrice;
    @InjectView(R.id.switchView)
    protected SwitchCompat switchCompat;
    @InjectView(R.id.recyclerViewFilters)
    protected EmptyRecyclerView recyclerViewFilters;
    @InjectView(R.id.switchHint)
    protected TextView switchHint;

    protected BaseArrayListAdapter<DtlAttribute> filtersAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerViewFilters.setLayoutManager(layoutManager);

        this.filtersAdapter = new BaseArrayListAdapter<>(getActivity(), this);
        this.filtersAdapter.registerCell(DtlAttribute.class, DtlFilterAttributeCell.class);

        recyclerViewFilters.setAdapter(filtersAdapter);

        rangeBarDistance.setOnRangeBarChangeListener((rangeBar, leftIndex, rightIndex, leftValue, rightValue) ->
                getPresenter().distanceChanged(Integer.valueOf(rightValue)));
        rangeBarPrice.setOnRangeBarChangeListener((rangeBar, leftIndex, rightIndex, leftValue, rightValue) ->
                getPresenter().priceChanged(Integer.valueOf(leftValue), Integer.valueOf(rightValue)));
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> getPresenter().distanceToggle());
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
        rangeBarDistance.setEnabled(filterData.isDistanceEnabled());
        switchCompat.setChecked(filterData.getDistance().isSelected());
        switchHint.setText(Html.fromHtml(getString(filterData.getDistance().getTextResId())));

        filtersAdapter.setItems(filterData.getAmenities());
    }
}
