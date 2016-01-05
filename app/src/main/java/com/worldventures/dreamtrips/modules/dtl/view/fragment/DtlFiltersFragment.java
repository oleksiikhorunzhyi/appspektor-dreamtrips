package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.appyvet.rangebar.RangeBar;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.selectable.MultiSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.SelectableHeaderItem;
import com.worldventures.dreamtrips.modules.common.view.util.DrawerListener;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlFiltersPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

import java.util.List;

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
    protected SwitchCompat distanceTypeSwitch;
    @InjectView(R.id.recyclerViewFilters)
    protected RecyclerView recyclerView;
    //
    MultiSelectionManager selectionManager;
    //
    protected BaseDelegateAdapter baseDelegateAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        //
        baseDelegateAdapter = new BaseDelegateAdapter<>(getActivity(), this);
        baseDelegateAdapter.registerCell(SelectableHeaderItem.class, DtlFilterAttributeHeaderCell.class);
        baseDelegateAdapter.registerCell(DtlMerchantAttribute.class, DtlFilterAttributeCell.class);
        baseDelegateAdapter.registerDelegate(SelectableHeaderItem.class, filterHeaderClickDelegate);
        baseDelegateAdapter.registerDelegate(DtlMerchantAttribute.class, filterItemClickDelegate);
        //
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //
        selectionManager = new MultiSelectionManager(recyclerView);
        selectionManager.setEnabled(true);
        //
        recyclerView.setAdapter(selectionManager.provideWrappedAdapter(baseDelegateAdapter));
        //
        ((MainActivity) getActivity()).attachRightDrawerListener(this);
    }

    @OnClick(R.id.apply)
    void onApply() {
        //TODO Think about drawers without dependency of MainActivity
        ((MainActivity) getActivity()).closeRightDrawer();
        getPresenter().apply(composeFilterData());
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

    private CellDelegate<SelectableHeaderItem> filterHeaderClickDelegate = new CellDelegate<SelectableHeaderItem>() {
        @Override
        public void onCellClicked(SelectableHeaderItem model) {
            selectionManager.setSelectionForAll(model.isSelected());
        }
    };

    private CellDelegate<DtlMerchantAttribute> filterItemClickDelegate = new CellDelegate<DtlMerchantAttribute>() {
        @Override
        public void onCellClicked(DtlMerchantAttribute model) {
            drawHeaderSelection();
        }
    };

    private void drawHeaderSelection() {
        boolean allSelected =
                selectionManager.isAllSelected(baseDelegateAdapter.getClassItemViewType(DtlMerchantAttribute.class));
        selectionManager.setSelection(0, allSelected);
    }

    /**
     * Compose new filter data from current views' state
     *
     * @return actual filter data
     */
    private DtlFilterData composeFilterData() {
        DtlFilterData data = DtlFilterData.createDefault();
        data.setPrice(Integer.valueOf(rangeBarPrice.getLeftValue()), Integer.valueOf(rangeBarPrice.getRightValue()));
        data.setDistanceType(distanceTypeSwitch.isChecked() ? DtlFilterData.DistanceType.KMS :
                DtlFilterData.DistanceType.MILES);
        data.setCurrentDistance(Integer.valueOf(rangeBarDistance.getRightValue()));
        //
        List<Integer> positions = selectionManager
                .getSelectedPositions(baseDelegateAdapter.getClassItemViewType(DtlMerchantAttribute.class));
        List selectedItems = Queryable.from(baseDelegateAdapter.getItems())
                .filter((element, index) -> positions.contains(index)).toList();
        data.setSelectedAmenities(selectedItems);
        return data;
    }

    @Override
    public void attachFilterData(DtlFilterData filterData) {
        if (filterData.hasAmenities()) setupAttributesHeader(filterData);
        syncUi(filterData);
    }

    @Override
    public void syncUi(DtlFilterData filterData) {
        rangeBarDistance.setRangePinsByValue(10f, filterData.getMaxDistance());
        rangeBarPrice.setRangePinsByValue(filterData.getMinPrice(), filterData.getMaxPrice());
        distanceTypeSwitch.setChecked(filterData.getDistanceType() == DtlFilterData.DistanceType.KMS);
        if (filterData.hasAmenities()) {
            selectionManager.setSelectedPositions(Queryable.from(filterData.getSelectedAmenities())
                    .map(element -> baseDelegateAdapter.getItems().indexOf(element)).toList());
        }
        drawHeaderSelection();
    }

    private void setupAttributesHeader(DtlFilterData filterData) {
        baseDelegateAdapter.clearAndUpdateItems(filterData.getAmenities());
        baseDelegateAdapter.addItem(0, new SelectableHeaderItem(getString(R.string.dtl_amenities), true));
        drawHeaderSelection();
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) getActivity()).detachRightDrawerListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDrawerOpened() {
        getPresenter().requestActualFilterData();
    }

    @Override
    public void onDrawerClosed() {
        // do nothing?
    }
}
