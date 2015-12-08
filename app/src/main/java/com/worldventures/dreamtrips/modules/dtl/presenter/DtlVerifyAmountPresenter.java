package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

import javax.inject.Inject;

public class DtlVerifyAmountPresenter extends Presenter<DtlVerifyAmountPresenter.View> {

    @Inject
    SnappyRepository snapper;

    private final DtlMerchant DtlMerchant;
    private DtlTransaction dtlTransaction;

    public DtlVerifyAmountPresenter(DtlMerchant place) {
        this.DtlMerchant = place;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(DtlMerchant.getId());
        view.attachTransaction(dtlTransaction);
        view.attachDtPoints(Double.valueOf(dtlTransaction.getPoints()).intValue());
    }

    public void rescan() {
        photoUploadingSpiceManager.cancelUploading(dtlTransaction.getUploadTask());
        dtlTransaction.setUploadTask(null);

        snapper.saveDtlTransaction(DtlMerchant.getId(), dtlTransaction);

        view.openScanReceipt(dtlTransaction);
    }

    public void scanQr() {
        dtlTransaction.setVerified(true);

        snapper.saveDtlTransaction(DtlMerchant.getId(), dtlTransaction);

        view.openScanQr(dtlTransaction);
    }

    public interface View extends Presenter.View {
        void attachTransaction(DtlTransaction dtlTransaction);

        void attachDtPoints(int count);

        void openScanReceipt(DtlTransaction dtlTransaction);

        void openScanQr(DtlTransaction dtlTransaction);
    }
}
