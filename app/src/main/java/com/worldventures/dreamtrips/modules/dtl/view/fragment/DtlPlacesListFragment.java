package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
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

@Layout(R.layout.fragment_dtl_places_list)
public class DtlPlacesListFragment
        extends RxBaseFragment<DtlMerchantListPresenter>
        implements DtlMerchantListPresenter.View {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    @InjectView(R.id.place_holder_offers)
    protected TextView emptyTextView;
    //
    BaseArrayListAdapter<DtlMerchant> adapter;
    RecyclerView.Adapter wrappedAdapter;
    //
    RecyclerViewStateDelegate stateDelegate;
    //
    SelectionManager selectionManager;

    private WeakHandler weakHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
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
        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider.get());
        adapter.registerCell(DtlMerchant.class, DtlMerchantCell.class);

        selectionManager = new SingleSelectionManager(recyclerView);
        selectionManager.setEnabled(isTabletLandscape());
        //
        wrappedAdapter = selectionManager.provideWrappedAdapter(adapter);
        recyclerView.setAdapter(selectionManager.provideWrappedAdapter(adapter));
        recyclerView.setEmptyView(emptyView);
        //
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        // we use SwipeRefreshLayout only for loading indicator, so disable manual triggering by user
        refreshLayout.setEnabled(false);
    }

    @Override
    public void setItems(List<DtlMerchant> places) {
        if (places != null && !places.isEmpty()) hideProgress();
        //
        adapter.setItems(places);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void showProgress() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void hideProgress() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void toggleSelection(DtlMerchant DtlMerchant) {
        int index = adapter.getItems().indexOf(DtlMerchant);
        if (index != -1)
            selectionManager.toggleSelection(index);
    }

    @Override
    public void setComingSoon() {
        emptyTextView.setText(R.string.dtl_coming_soon_offers);
    }

    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        selectionManager.release();
        super.onDestroyView();
    }

}
