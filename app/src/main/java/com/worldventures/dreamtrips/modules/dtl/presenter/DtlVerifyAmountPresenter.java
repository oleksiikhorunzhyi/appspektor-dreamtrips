package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

public class DtlVerifyAmountPresenter extends Presenter<DtlVerifyAmountPresenter.View> {

    private final String merchantId;

    @Inject
    SnappyRepository snapper;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    private DtlMerchant dtlMerchant;

    private DtlTransaction dtlTransaction;

    public DtlVerifyAmountPresenter(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchant = dtlMerchantManager.getMerchantById(merchantId);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(merchantId);
        view.attachTransaction(dtlTransaction, dtlMerchant.getDefaultCurrency());
        view.attachDtPoints(Double.valueOf(dtlTransaction.getPoints()).intValue());
    }

    public void rescan() {
        photoUploadingManager.cancelUpload(dtlTransaction.getUploadTask());
        dtlTransaction.setUploadTask(null);

        snapper.saveDtlTransaction(merchantId, dtlTransaction);

        view.openScanReceipt(dtlTransaction);
    }

    public void scanQr() {
        dtlTransaction.setVerified(true);

        snapper.saveDtlTransaction(merchantId, dtlTransaction);

        view.openScanQr(dtlTransaction);
    }

    public interface View extends Presenter.View {
        void attachTransaction(DtlTransaction dtlTransaction, DtlCurrency dtlCurrency);

        void attachDtPoints(int count);

        void openScanReceipt(DtlTransaction dtlTransaction);

        void openScanQr(DtlTransaction dtlTransaction);
    }
}
