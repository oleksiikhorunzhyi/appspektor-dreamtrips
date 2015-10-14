package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.appyvet.rangebar.RangeBar;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlFiltersPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_filters)
public class DtlFiltersFragment extends BaseFragment<DtlFiltersPresenter> implements DtlFiltersPresenter.View {

    @InjectView(R.id.range_bar_distance)
    protected RangeBar rangeBarDistance;
    @InjectView(R.id.range_bar_price)
    protected RangeBar rangeBarPrice;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        rangeBarDistance.setOnRangeBarChangeListener((rangeBar, leftIndex, rightIndex, leftValue, rightValue) ->
                getPresenter().distanceChanged(Integer.valueOf(leftValue), Integer.valueOf(rightValue)));
        rangeBarPrice.setOnRangeBarChangeListener((rangeBar, leftIndex, rightIndex, leftValue, rightValue) ->
                getPresenter().priceChanged(Integer.valueOf(leftValue), Integer.valueOf(rightValue)));
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
}
