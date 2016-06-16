package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.adapter.expandable.ExpandableLayoutManager;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.selectable.SelectionManager;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantCellDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbarHelper;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.ExpandableDtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;

public class DtlMerchantsScreenImpl
        extends DtlLayout<DtlMerchantsScreen, DtlMerchantsPresenter, DtlMerchantsPath>
        implements DtlMerchantsScreen, DtlMerchantCellDelegate {

    @Optional
    @InjectView(R.id.expandableDtlToolbar)
    ExpandableDtlToolbar dtlToolbar;
    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView)
    View emptyView;
    //
    BaseDelegateAdapter baseDelegateAdapter;
    SelectionManager selectionManager;
    WeakHandler weakHandler;

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
        weakHandler = new WeakHandler(Looper.getMainLooper());
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
        refreshLayout.setEnabled(false);
    }

    private void initDtlToolbar() {
        if (dtlToolbar == null) return;
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
        RxDtlToolbar.filterButtonClicks(dtlToolbar)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aVoid -> ((FlowActivity) getActivity()).openRightDrawer());
    }

    @Override
    public void setFilterButtonState(boolean enabled) {
        if (dtlToolbar == null) return;
        dtlToolbar.setFilterEnabled(enabled);
    }

    @Override
    public void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation) {
        if (dtlToolbar == null) return;
        dtlToolbar.setLocationCaption(DtlToolbarHelper
                .provideLocationCaption(getResources(), dtlLocation));
    }

    @Override
    public void updateToolbarSearchCaption(@Nullable String searchCaption) {
        if (dtlToolbar == null) return;
        dtlToolbar.setSearchCaption(searchCaption);
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
        weakHandler.post(() -> baseDelegateAdapter.setItems(merchants));
    }

    @Override
    public Observable<Boolean> getToggleObservable() {
        if (dtlToolbar == null) return Observable.empty();
        return RxDtlToolbar.diningFilterChanges(dtlToolbar)
                .compose(RxLifecycle.bindView(this));
    }

    @Override
    public void showProgress() {
        weakHandler.post(() -> {
            refreshLayout.setRefreshing(true);
            emptyView.setVisibility(GONE);
        });
    }

    @Override
    public void hideProgress() {
        weakHandler.post(() -> {
            refreshLayout.setRefreshing(false);
            emptyView.setVisibility(VISIBLE);
        });
    }

    @Override
    public void toggleDiningFilterSwitch(boolean enabled) {
        if (dtlToolbar == null) return;
        dtlToolbar.toggleDiningFilterSwitch(enabled);
    }

    @Override
    public void toggleSelection(DtlMerchant DtlMerchant) {
        int index = baseDelegateAdapter.getItems().indexOf(DtlMerchant);
        if (index != -1) selectionManager.toggleSelection(index);
    }

    @Override
    public boolean isToolbarCollapsed() {
        return dtlToolbar == null || dtlToolbar.isCollapsed();
    }

    @Override
    protected void onDetachedFromWindow() {
        selectionManager.release();
        recyclerView.setAdapter(null);
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
