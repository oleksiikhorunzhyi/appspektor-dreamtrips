package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.place.RatePlaceRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;

import javax.inject.Inject;

import icepick.State;

public class DtlTransactionSucceedPresenter extends Presenter<DtlTransactionSucceedPresenter.View> {

    private final DTlMerchant DTlMerchant;
    private DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;

    @State
    int stars;

    public DtlTransactionSucceedPresenter(DTlMerchant DTlMerchant) {
        this.DTlMerchant = DTlMerchant;
    }

    public void rate(int stars) {
        this.stars = stars;
    }

    public void share() {
        view.showShareDialog((int) dtlTransaction.getDtlTransactionResult().getEarnedPoints(), DTlMerchant);
    }

    public void done() {
        if (stars != 0)
            doRequest(new RatePlaceRequest(DTlMerchant.getId(),
                    stars, dtlTransaction.getDtlTransactionResult().getId()));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(DTlMerchant.getId());
        view.setCongratulations(dtlTransaction.getDtlTransactionResult());
    }

    public interface View extends Presenter.View {
        void showShareDialog(int amount, DTlMerchant DTlMerchant);

        void setCongratulations(DtlTransactionResult result);
    }
}
