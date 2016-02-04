package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.store.DtlJobManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import javax.inject.Inject;

import icepick.State;

public class DtlTransactionSucceedPresenter extends JobPresenter<DtlTransactionSucceedPresenter.View> {

    private final String merchantId;
    private DtlMerchant dtlMerchant;
    private DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;
    @Inject
    DtlMerchantRepository dtlMerchantRepository;
    @Inject
    DtlJobManager jobManager;

    @State
    int stars;

    public DtlTransactionSucceedPresenter(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchant = dtlMerchantRepository.getMerchantById(merchantId);
    }

    public void rate(int stars) {
        this.stars = stars;
    }

    public void share() {
        view.showShareDialog((int) dtlTransaction.getDtlTransactionResult().getEarnedPoints(), dtlMerchant);
    }

    public void done() {
        if (stars != 0)
            jobManager.rateExecutor
                    .createJobWith(merchantId, stars, dtlTransaction.getDtlTransactionResult().getId())
                    .subscribe();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        dtlTransaction = snapper.getDtlTransaction(merchantId);
        view.setCongratulations(dtlTransaction.getDtlTransactionResult());
        bindApiJob();
    }

    private void bindApiJob() {
        bindJobCached(jobManager.rateExecutor)
                .onError(throwable -> apiErrorPresenter.handleError(throwable));
    }

    /**
     * Analytic-related
     */
    public void trackSharing(@ShareFragment.ShareType String type) {
        TrackingHelper.dtlShare(type);
    }

    public interface View extends ApiErrorView, RxView {
        void showShareDialog(int amount, DtlMerchant DtlMerchant);

        void setCongratulations(DtlTransactionResult result);
    }
}
