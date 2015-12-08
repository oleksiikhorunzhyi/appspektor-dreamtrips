package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.dtl.api.merchant.RateMerchantRequest;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;

import javax.inject.Inject;

import icepick.State;

public class DtlTransactionSucceedPresenter extends Presenter<DtlTransactionSucceedPresenter.View> {

    private final DtlMerchant DtlMerchant;
    private DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;

    @State
    int stars;

    public DtlTransactionSucceedPresenter(DtlMerchant DtlMerchant) {
        this.DtlMerchant = DtlMerchant;
    }

    public void rate(int stars) {
        this.stars = stars;
    }

    public void share() {
        view.showShareDialog((int) dtlTransaction.getDtlTransactionResult().getEarnedPoints(), DtlMerchant);
    }

    public void done() {
        if (stars != 0)
            doRequest(new RateMerchantRequest(DtlMerchant.getId(),
                    stars, dtlTransaction.getDtlTransactionResult().getId()));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(DtlMerchant.getId());
        view.setCongratulations(dtlTransaction.getDtlTransactionResult());
    }

    /**
     * Analytic-related
     */
    public void trackSharing(@ShareFragment.ShareType String type) {
        TrackingHelper.dtlShare(type);
    }

    public interface View extends Presenter.View {
        void showShareDialog(int amount, DtlMerchant DtlMerchant);

        void setCongratulations(DtlTransactionResult result);
    }
}
