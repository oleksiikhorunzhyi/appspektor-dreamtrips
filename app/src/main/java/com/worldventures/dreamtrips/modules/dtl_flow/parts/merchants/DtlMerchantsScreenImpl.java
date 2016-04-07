package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.selectable.SelectionManager;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.dtl.helper.SearchViewHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantCellNew;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import icepick.State;
import rx.Observable;

public class DtlMerchantsScreenImpl extends DtlLayout<DtlMerchantsScreen, DtlMerchantsPresenter, DtlMerchantsPath>
        implements DtlMerchantsScreen, CellDelegate<DtlMerchant> {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    //
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.filterDiningsSwitch)
    SwitchCompat filterDiningsSwitch;
    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    @InjectView(R.id.merchant_holder_offers)
    protected TextView emptyTextView;
    //
    BaseDelegateAdapter<DtlMerchant> baseDelegateAdapter;
    SearchViewHelper searchViewHelper;
    //
//    RecyclerViewStateDelegate stateDelegate; // TODO :: 4/2/16 deal with this state delegate stuff?
    //
    SelectionManager selectionManager;
    //
    @State
    String lastQuery;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //
        baseDelegateAdapter = new BaseDelegateAdapter<>(getActivity(), injectorProvider.get());
        baseDelegateAdapter.registerCell(DtlMerchant.class, DtlMerchantCellNew.class);
        baseDelegateAdapter.registerDelegate(DtlMerchant.class, this);
        //
        selectionManager = new SingleSelectionManager(recyclerView);
        selectionManager.setEnabled(isTabletLandscape());
        //
        recyclerView.setAdapter(selectionManager.provideWrappedAdapter(baseDelegateAdapter));
        recyclerView.setEmptyView(emptyView);
        //
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        // we use SwipeRefreshLayout only for loading indicator, so disable manual triggering by user
        refreshLayout.setEnabled(false);
        //
//        stateDelegate = new RecyclerViewStateDelegate();
//        stateDelegate.setRecyclerView(recyclerView);
//        stateDelegate.onCreate(savedInstanceState);
//        stateDelegate.saveStateIfNeeded(outState);
        //
        toolbar.inflateMenu(getPresenter().getToolbarMenuRes());
        searchViewHelper = new SearchViewHelper();
        searchViewHelper.init(toolbar.getMenu().findItem(R.id.action_search), lastQuery,
                query -> {
                    lastQuery = query;
                    getPresenter().applySearch(query);
                }, null);
        toolbar.setOnMenuItemClickListener(getPresenter()::onToolbarMenuItemClick);
        initToolbar();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation) {
        if (dtlLocation == null || toolbar == null) return; // for safety reasons
        //
        TextView locationTitle = ButterKnife.<TextView>findById(toolbar, R.id.spinnerStyledTitle);
        TextView locationModeCaption = ButterKnife.<TextView>findById(toolbar, R.id.locationModeCaption);
        //
        if (locationTitle == null || locationModeCaption == null) return;
        //
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
            case EXTERNAL:
                locationTitle.setText(dtlLocation.getLongName());
                locationModeCaption.setVisibility(View.GONE);
                break;
            case FROM_MAP:
                if (dtlLocation.getLongName() == null) {
                    locationModeCaption.setVisibility(View.GONE);
                    locationTitle.setText(R.string.dtl_nearby_caption);
                } else {
                    locationModeCaption.setVisibility(View.VISIBLE);
                    locationTitle.setText(dtlLocation.getLongName());
                }
                break;
        }
    }

    private void initToolbar() {
        // TODO move to delegate
        if (!isTabletLandscape()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            toolbar.setNavigationOnClickListener(view -> ((FlowActivity) getActivity()).openLeftDrawer());
        }
        //
        ButterKnife.findById(toolbar, R.id.titleContainer).setOnClickListener(v ->
                getPresenter().onToolbarTitleClicked()
        );
    }

    @Override
    public void openRightDrawer() {
        ((FlowActivity) getActivity()).openRightDrawer();
    }

    @Override
    public void onCellClicked(DtlMerchant merchant) {
        getPresenter().merchantClicked(merchant);
    }

    @Override
    public void setItems(List<DtlMerchant> merchants) {
//        if (merchants != null && !merchants.isEmpty()) hideProgress();
        hideProgress();
        //
        baseDelegateAdapter.setItems(merchants);
//        stateDelegate.restoreStateIfNeeded();
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
    protected void onDetachedFromWindow() {
//        stateDelegate.onDestroyView();
        searchViewHelper.dropHelper();
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
