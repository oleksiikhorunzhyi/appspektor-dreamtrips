package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.adapter.expandable.ExpandableLayoutManager;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.selectable.SelectionManager;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantCellDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlFilterButton;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;

public class DtlMerchantsScreenImpl
        extends DtlLayout<DtlMerchantsScreen, DtlMerchantsPresenter, DtlMerchantsPath>
        implements DtlMerchantsScreen, DtlMerchantCellDelegate {

    @InjectView(R.id.dtlToolbar)
    DtlToolbar dtlToolbar;
    @InjectView(R.id.filterDiningsSwitch)
    SwitchCompat filterDiningsSwitch;
    @InjectView(R.id.dtlfb_rootView)
    DtlFilterButton filtersButton;
    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView)
    View emptyView;
    @InjectView(R.id.filterBarRoot)
    View filterRoot;
    //
    BaseDelegateAdapter baseDelegateAdapter;
    SelectionManager selectionManager;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        recyclerView.setLayoutManager(new ExpandableLayoutManager(getActivity()));
    }

    @Override
    protected void onPostAttachToWindowView() {
        super.onPostAttachToWindowView();
        initDtlToolbar();
        //
        baseDelegateAdapter = new BaseDelegateAdapter(getActivity(), injector);
        baseDelegateAdapter.registerCell(DtlMerchant.class, DtlMerchantExpandableCell.class);
        baseDelegateAdapter.registerDelegate(DtlMerchant.class, this);
        //
        selectionManager = new SingleSelectionManager(recyclerView);
        selectionManager.setEnabled(isTabletLandscape());
        //
        recyclerView.setAdapter(baseDelegateAdapter);
        recyclerView.setEmptyView(emptyView);
        //
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        // we use SwipeRefreshLayout only for loading indicator - disable manual triggering by user
        refreshLayout.setEnabled(false);
        //
        ViewUtils.setViewVisibility(isTabletLandscape() ? View.GONE : View.VISIBLE, filterRoot); // TODO :: fix after DT-1718 finished
    }

    private void initDtlToolbar() {
        RxDtlToolbar.actionViewClicks(dtlToolbar)
                .throttleFirst(250L, TimeUnit.MILLISECONDS)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aVoid -> ((FlowActivity) getActivity()).openLeftDrawer());
        RxDtlToolbar.navigationClicks(dtlToolbar)
                .throttleFirst(250L, TimeUnit.MILLISECONDS)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aVoid -> getPresenter().mapClicked());
        RxDtlToolbar.merchantSearchTextChanges(dtlToolbar)
                .debounce(250L, TimeUnit.MILLISECONDS)
                .skipWhile(TextUtils::isEmpty)
                .filter(s -> !dtlToolbar.isCollapsed())
                .compose(RxLifecycle.bindView(this))
                .subscribe(getPresenter()::applySearch);
        RxDtlToolbar.locationInputFocusChanges(dtlToolbar)
                .skip(1)
                .compose(RxLifecycle.bindView(this))
                .filter(Boolean::booleanValue) // only true -> only focus gains
                .subscribe(aBoolean -> getPresenter().locationChangeRequested());
    }

    @Override
    public void setFilterButtonState(boolean enabled) {
        filtersButton.setFilterEnabled(enabled);
    }

    @Override
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation,
                                   @Nullable String actualSearchQuery) {
        if (dtlLocation == null) return;
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
            case EXTERNAL:
                dtlToolbar.setToolbarCaptions(actualSearchQuery, dtlLocation.getLongName());
                break;
            case FROM_MAP:
                String locationTitle = TextUtils.isEmpty(dtlLocation.getLongName()) ?
                        getResources().getString(R.string.dtl_nearby_caption_empty) :
                        getResources().getString(R.string.dtl_nearby_caption_format,
                                dtlLocation.getLongName());
                dtlToolbar.setToolbarCaptions(actualSearchQuery, locationTitle);
                break;
        }
    }

    @OnClick(R.id.dtlfb_rootView)
    void onFiltersCounterClicked(View view) {
        ((FlowActivity) getActivity()).openRightDrawer();
    }

    @Override
    public void onCellClicked(DtlMerchant merchant) {
        getPresenter().merchantClicked(merchant);
    }

    @Override
    public void onExpandedToggle(int position) {
        baseDelegateAdapter.notifyItemChanged(position);
    }

    @Override
    public void onOfferClick(DtlMerchant dtlMerchant, DtlOffer dtlOffer) {
        getPresenter().onOfferClick(dtlMerchant, dtlOffer);
    }

    @Override
    public void setItems(List<DtlMerchant> merchants) {
        hideProgress();
        baseDelegateAdapter.setItems(merchants);
    }

    @Override
    public Observable<Boolean> getToggleObservable() {
        return RxCompoundButton.checkedChanges(filterDiningsSwitch)
                .compose(RxLifecycle.bindView(this));
    }

    @Override
    public void showProgress() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void toggleDiningFilterSwitch(boolean checked) {
        filterDiningsSwitch.setChecked(checked);
    }

    @Override
    public void toggleSelection(DtlMerchant DtlMerchant) {
        int index = baseDelegateAdapter.getItems().indexOf(DtlMerchant);
        if (index != -1) selectionManager.toggleSelection(index);
    }

    @Override
    public boolean isToolbarCollapsed() {
        return dtlToolbar.isCollapsed();
    }

    @Override
    protected void onDetachedFromWindow() {
        selectionManager.release();
        super.onDetachedFromWindow();
    }

    @Override
    public void onApiCallFailed() {
        super.onApiCallFailed();
        hideProgress();
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getContext().getString(R.string.alert))
                .setContentText(errorResponse.getFirstMessage())
                .setConfirmText(getContext().getString(R.string.ok))
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        alertDialog.setCancelable(false);
        alertDialog.show();
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Boilerplate stuff
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DtlMerchantsPresenter createPresenter() {
        return new DtlMerchantsPresenterImpl(getContext(), injector);
    }

    public DtlMerchantsScreenImpl(Context context) {
        super(context);
    }

    public DtlMerchantsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
