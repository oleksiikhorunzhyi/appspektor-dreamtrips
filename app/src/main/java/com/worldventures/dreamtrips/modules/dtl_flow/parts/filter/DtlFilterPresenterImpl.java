package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import java.util.List;

import javax.inject.Inject;

import techery.io.library.JobSubscriber;

public class DtlFilterPresenterImpl implements DtlFilterPresenter {

    @Inject
    DtlMerchantManager dtlMerchantManager;

    protected FilterView view;

    @Override
    public void onDrawerToggle(boolean show) {
        if (view != null && show) view.syncUi(dtlMerchantManager.getFilterData());
    }

    protected void closeDrawer() {
        if (view != null) view.toggleDrawer(false);
    }

    private void attachAmenities() {
        view.attachFilterData(dtlMerchantManager.getFilterData());
    }

    @Override
    public void attachView(FilterView view) {
        this.view = view;
        this.view.getInjector().inject(this);
        connectFilter();
    }

    @Override
    public void detachView(boolean retainInstance) {
        view = null;
    }

    @Override
    public void apply() {
        dtlMerchantManager.applyFilter(view.getFilterParameters());
        closeDrawer();
    }

    @Override
    public void resetAll() {
        dtlMerchantManager.reset();
        closeDrawer();
    }

    private void connectFilter() {
        dtlMerchantManager.connectMerchantsWithCache()
                .compose(new IoToMainComposer<>())
                .subscribe(new JobSubscriber<List<DtlMerchant>>()
                        .onSuccess(dtlMerchants -> attachAmenities()));
    }
}
