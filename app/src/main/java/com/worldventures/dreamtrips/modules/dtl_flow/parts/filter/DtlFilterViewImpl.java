package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.hannesdorfmann.mosby.mvp.layout.MvpLinearLayout;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.MultiSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.SelectableHeaderItem;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlFilterAttributeCell;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.filter.rx.DrawerToggleObservable;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DtlFilterAttributeHeaderCell;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DtlFilterViewImpl extends MvpLinearLayout<FilterView, DtlFilterPresenter> implements FilterView {

    @InjectView(R.id.range_bar_distance)
    protected RangeBar rangeBarDistance;
    @InjectView(R.id.range_bar_price)
    protected RangeBar rangeBarPrice;
    @InjectView(R.id.distance_filter_caption)
    protected TextView distanceCaption;
    @InjectView(R.id.recyclerViewFilters)
    protected RecyclerView recyclerView;
    //
    protected MultiSelectionManager selectionManager;
    //
    protected BaseDelegateAdapter baseDelegateAdapter;

    private Injector injector;

    public DtlFilterViewImpl(Context context) {
        super(context);
        init(context);
    }

    public DtlFilterViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DtlFilterViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DtlFilterViewImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public DtlFilterPresenter createPresenter() {
        return new DtlFilterPresenterImpl();
    }

    private void init(Context context) {
        ButterKnife.inject(this, LayoutInflater.from(context).inflate(R.layout.fragment_dtl_filters, this, true));
    }

    private void connectDrawerListener() {
        DrawerToggleObservable.create(ButterKnife.<DrawerLayout>findById(getRootView(), R.id.drawer))
                .compose(RxLifecycle.bindView(this))
                .subscribe(this::onDrawerToggle);
    }

    private void onDrawerToggle(int gravity) {
        getPresenter().onDrawerToggle(gravity == Gravity.RIGHT);
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
        attachAdapter();
        connectDrawerListener();
    }

    private void attachAdapter() {
        setupAdapter();
        setupRecyclerView();
    }

    protected void setupRecyclerView() {
        selectionManager = new MultiSelectionManager(recyclerView);
        selectionManager.setEnabled(true);
        //
        recyclerView.setLayoutManager(provideLayoutManager());
        recyclerView.setAdapter(selectionManager.provideWrappedAdapter(baseDelegateAdapter));
    }

    protected void setupAdapter() {
        if (injector == null) throw new NullPointerException("Set injector before setup adapter");
        //
        baseDelegateAdapter = new BaseDelegateAdapter<>(getContext(), injector);
        baseDelegateAdapter.registerCell(SelectableHeaderItem.class, DtlFilterAttributeHeaderCell.class);
        baseDelegateAdapter.registerCell(DtlMerchantAttribute.class, DtlFilterAttributeCell.class);
        baseDelegateAdapter.registerDelegate(SelectableHeaderItem.class, model -> selectionManager.setSelectionForAll(((SelectableHeaderItem) model).isSelected()));
        baseDelegateAdapter.registerDelegate(DtlMerchantAttribute.class, model -> drawHeaderSelection());
    }

    @OnClick(R.id.apply)
    void onApply() {
        getPresenter().apply();
    }

    @OnClick(R.id.reset)
    void onReset() {
        getPresenter().resetAll();
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    @Override
    public void toggleDrawer(boolean show) {
        DrawerLayout drawer = ButterKnife.<DrawerLayout>findById(getRootView(), R.id.drawer);
        if (drawer == null) return;

        if(show) drawer.openDrawer(Gravity.RIGHT);
        else drawer.closeDrawer(Gravity.RIGHT);
    }

    @Override
    public DtlFilterParameters getFilterParameters() {
        return ImmutableDtlFilterParameters.builder()
                .minPrice(Integer.valueOf(rangeBarPrice.getLeftValue()))
                .maxPrice(Integer.valueOf(rangeBarPrice.getRightValue()))
                .maxDistance(Integer.valueOf(rangeBarDistance.getRightValue()))
                .selectedAmenities(obtainSelectedAmenities())
                .build();
    }

    @Override
    public void attachFilterData(DtlFilterData filterData) {
        if (filterData.hasAmenities()) setupAttributesHeader(filterData);
        syncUi(filterData);
    }

    @Override
    public void syncUi(DtlFilterData filterData) {
        rangeBarDistance.setRangePinsByValue(10f, (float) filterData.getMaxDistance());
        rangeBarPrice.setRangePinsByValue(filterData.getMinPrice(), filterData.getMaxPrice());
        distanceCaption.setText(getContext().getString(R.string.dtl_distance,
                getContext().getString(filterData.getDistanceType() == DistanceType.MILES ?
                        R.string.mi : R.string.km)));
        updateSelection(filterData);
        drawHeaderSelection();
    }

    private List<DtlMerchantAttribute> obtainSelectedAmenities() {
        List<Integer> positions = selectionManager
                .getSelectedPositions(baseDelegateAdapter.getClassItemViewType(DtlMerchantAttribute.class));
        return Queryable.from(baseDelegateAdapter.getItems())
                .filter((element, index) -> positions.contains(index)).toList();
    }

    private void updateSelection(DtlFilterData filterData) {
        if (filterData.hasAmenities()) {
            selectionManager.setSelectedPositions(Queryable.from(filterData.getSelectedAmenities())
                    .map(element -> baseDelegateAdapter.getItems().indexOf(element)).toList());
        }
    }

    private void drawHeaderSelection() {
        final int amenityViewTypeId = baseDelegateAdapter.getClassItemViewType(DtlMerchantAttribute.class);
        final boolean allSelected = selectionManager.isAllSelected(amenityViewTypeId);
        selectionManager.setSelection(0, allSelected);
    }

    private void setupAttributesHeader(DtlFilterData filterData) {
        baseDelegateAdapter.clearAndUpdateItems(filterData.getAmenities());
        baseDelegateAdapter.addItem(0, new SelectableHeaderItem(getContext().getString(R.string.dtl_amenities), true));
    }

    protected RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(getContext());
    }
}
