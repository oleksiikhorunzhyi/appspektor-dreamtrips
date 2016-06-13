package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class DtlFilterPresenterImpl implements DtlFilterPresenter {

    @Inject
    DtlFilterMerchantInteractor filterInteractor;

    protected FilterView view;

    PublishSubject<Void> detachStopper = PublishSubject.create();

    protected void closeDrawer() {
        if (view != null) view.toggleDrawer(false);
    }

    @Override
    public void attachView(FilterView view) {
        this.view = view;
        this.view.getInjector().inject(this);
        updateFilterState();
        bindFilterUpdates();
    }

    @Override
    public void detachView(boolean retainInstance) {
        view = null;
        detachStopper.onNext(null);
    }

    @Override
    public void apply() {
        filterInteractor.filterDataPipe().send(DtlFilterDataAction.applyParams(view.getFilterParameters()));
        closeDrawer();
    }

    @Override
    public void resetAll() {
        filterInteractor.filterDataPipe().send(DtlFilterDataAction.reset());
        closeDrawer();
    }

    private void bindFilterUpdates() {
        filterInteractor.filterDataPipe()
                .observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(detachStopper.asObservable())
                .subscribe(view::syncUi);
    }

    private void updateFilterState() {
        filterInteractor.filterDataPipe()
                .observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::syncUi);
    }
}
