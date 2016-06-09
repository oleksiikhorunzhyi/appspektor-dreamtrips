package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.content.Context;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import javax.inject.Inject;

import rx.Observable;

public class MasterToolbarPresenterImpl
        extends DtlPresenterImpl<MasterToolbarScreen, ViewState.EMPTY>
        implements MasterToolbarPresenter {

    @Inject
    DtlFilterMerchantInteractor filterInteractor;
    @Inject
    DtlLocationInteractor locationInteractor;

    public MasterToolbarPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());
        //
        connectDtlLocationChanges();
        connectFilterDataChanges();
        //
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::isOffersOnly)
                .subscribe(getView()::toggleDiningFilterSwitch);
    }

    private void connectFilterDataChanges() {
        filterInteractor.filterDataPipe().observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData -> {
                    getView().setFilterButtonState(!dtlFilterData.isDefault());
                });
    }

    private void connectDtlLocationChanges() {
        Observable.combineLatest(
                locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
                        .map(DtlLocationCommand::getResult),
                filterInteractor.filterDataPipe().observeSuccessWithReplay()
                        .first()
                        .map(DtlFilterDataAction::getResult)
                        .map(DtlFilterData::getSearchQuery),
                Pair::new
        ).compose(bindViewIoToMainComposer())
                .subscribe(pair -> getView().updateToolbarTitle(pair.first, pair.second));
    }

    @Override
    public void applyOffersOnlyFilterState(boolean enabled) {
        filterInteractor.filterDataPipe()
                .send(DtlFilterDataAction.applyOffersOnly(enabled));
    }

    @Override
    public void applySearch(String query) {
        filterInteractor.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
    }
}
