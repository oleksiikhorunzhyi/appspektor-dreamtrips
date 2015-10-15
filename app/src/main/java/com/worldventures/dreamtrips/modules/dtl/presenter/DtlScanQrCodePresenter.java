package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.EarnPointsRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;

import javax.inject.Inject;

public class DtlScanQrCodePresenter extends Presenter<DtlScanQrCodePresenter.View> {

    private final DtlPlace dtlPlace;

    DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;

    public DtlScanQrCodePresenter(DtlPlace dtlPlace) {
        this.dtlPlace = dtlPlace;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(dtlPlace.getId());
        view.setPlace(dtlPlace);
    }

    public void codeScanned(String content) {
        dtlTransaction.setCode(content);
        view.showProgress();
        doRequest(new EarnPointsRequest(dtlPlace.getId(), dtlTransaction), this::processTransactionResult);
    }

    private void processTransactionResult(DtlTransactionResult result) {
        dtlTransaction.setDtlTransactionResult(result);
        snapper.saveDtlTransaction(dtlPlace.getId(), dtlTransaction);
        view.hideProgress();

        view.openTransactionSuccess(dtlPlace, dtlTransaction);
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.hideProgress();
    }

    public interface View extends Presenter.View {
        void openTransactionSuccess(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void showProgress();

        void hideProgress();

        void setPlace(DtlPlace dtlPlace);
    }
}
