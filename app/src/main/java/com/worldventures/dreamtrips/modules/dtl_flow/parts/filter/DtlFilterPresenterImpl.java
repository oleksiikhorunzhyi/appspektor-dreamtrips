package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantStore;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class DtlFilterPresenterImpl implements DtlFilterPresenter {

    @Inject
    Janet janet;
    @Inject
    DtlFilterMerchantStore filterStore;

    protected FilterView view;
    private WriteActionPipe<DtlFilterMerchantStoreAction> filterStorePipe;

    PublishSubject<Void> detachStopper = PublishSubject.create();

    @Override
    public void onDrawerToggle(boolean show) {
        if (view != null && show) {
            filterStore.getFilterDataState()
                    .subscribeOn(Schedulers.immediate())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::syncUi);
        }
    }

    protected void closeDrawer() {
        if (view != null) view.toggleDrawer(false);
    }

    @Override
    public void attachView(FilterView view) {
        this.view = view;
        this.view.getInjector().inject(this);
        filterStorePipe = janet.createPipe(DtlFilterMerchantStoreAction.class);
        connectFilter();
    }

    @Override
    public void detachView(boolean retainInstance) {
        view = null;
        detachStopper.onNext(null);
    }

    @Override
    public void apply() {
        filterStorePipe.send(DtlFilterMerchantStoreAction.applyParams(view.getFilterParameters()));
        closeDrawer();
    }

    @Override
    public void resetAll() {
        filterStorePipe.send(DtlFilterMerchantStoreAction.reset());
        closeDrawer();
    }

    private void connectFilter() {
        filterStore.observeStateChange()
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(detachStopper.asObservable())
        .subscribe(view::attachFilterData);
    }
}
