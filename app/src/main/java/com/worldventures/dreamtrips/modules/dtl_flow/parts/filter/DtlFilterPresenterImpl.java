package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class DtlFilterPresenterImpl implements DtlFilterPresenter {

    @Inject
    DtlFilterMerchantService filterService;

    protected FilterView view;

    PublishSubject<Void> detachStopper = PublishSubject.create();

    @Override
    public void onDrawerToggle(boolean show) {
        if (view != null && show) {
            filterService.getFilterData()
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
        connectFilter();
    }

    @Override
    public void detachView(boolean retainInstance) {
        view = null;
        detachStopper.onNext(null);
    }

    @Override
    public void apply() {
        filterService.filterDataPipe().send(DtlFilterDataAction.applyParams(view.getFilterParameters()));
        closeDrawer();
    }

    @Override
    public void resetAll() {
        filterService.filterDataPipe().send(DtlFilterDataAction.reset());
        closeDrawer();
    }

    private void connectFilter() {
        filterService.filterDataPipe()
                .observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(detachStopper.asObservable())
                .subscribe(view::attachFilterData);
    }
}
