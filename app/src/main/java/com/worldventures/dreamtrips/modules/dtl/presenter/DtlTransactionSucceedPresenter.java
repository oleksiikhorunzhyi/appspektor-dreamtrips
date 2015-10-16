package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.RatePlaceRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;

import javax.inject.Inject;

public class DtlTransactionSucceedPresenter extends Presenter<DtlTransactionSucceedPresenter.View> {

    private final DtlPlace dtlPlace;
    private DtlTransaction dtlTransaction;
    @Inject
    SnappyRepository snapper;

    public DtlTransactionSucceedPresenter(DtlPlace dtlPlace) {
        this.dtlPlace = dtlPlace;
    }

    public void rate(int stars) {
        view.showProgress();
        doRequest(new RatePlaceRequest(dtlPlace.getId(), stars), aVoid ->
                view.rateSucceed());
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.hideProgress();
    }

    public void share() {
        view.showShareDialog((int) dtlTransaction.getDtlTransactionResult().getEarnedPoints(), dtlPlace);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(dtlPlace.getId());
        view.setCongratulations(dtlTransaction.getDtlTransactionResult());
    }

    public interface View extends Presenter.View {
        void showShareDialog(int amount, DtlPlace dtlPlace);

        void setCongratulations(DtlTransactionResult result);

        void showProgress();

        void hideProgress();

        void rateSucceed();
    }
}
