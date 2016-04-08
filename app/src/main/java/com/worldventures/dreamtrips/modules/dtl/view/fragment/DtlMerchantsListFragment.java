package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.selectable.SelectionManager;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantListPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_merchants_list)
public class DtlMerchantsListFragment
        extends RxBaseFragment<DtlMerchantListPresenter>
        implements DtlMerchantListPresenter.View, CellDelegate<DtlMerchant> {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    //
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
    //
    RecyclerViewStateDelegate stateDelegate;
    //
    SelectionManager selectionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    protected DtlMerchantListPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMerchantListPresenter((DtlMerchantType) getArguments().getSerializable(EXTRA_TYPE));
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //
        baseDelegateAdapter = new BaseDelegateAdapter<>(getActivity(), injectorProvider.get());
        baseDelegateAdapter.registerCell(DtlMerchant.class, DtlMerchantCell.class);
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
    }

    @Override
    public void onCellClicked(DtlMerchant model) {
        // TODO think how to use CellDelegate here
    }

    @Override
    public void setItems(List<DtlMerchant> merchants) {
        if (merchants != null && !merchants.isEmpty()) hideProgress();
        //
        baseDelegateAdapter.setItems(merchants);
        stateDelegate.restoreStateIfNeeded();
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
    public void showMessage(@StringRes int textResourceId) {
        emptyTextView.setText(textResourceId);
    }

    @Override
    public void toggleSelection(DtlMerchant DtlMerchant) {
        int index = baseDelegateAdapter.getItems().indexOf(DtlMerchant);
        if (index != -1) selectionManager.toggleSelection(index);
    }

    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        selectionManager.release();
        super.onDestroyView();
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
    }
}
