package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

public class DtlVerifyAmountPresenter extends Presenter<DtlVerifyAmountPresenter.View> {

    @Inject
    SnappyRepository db;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    //
    private final String merchantId;
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
        dtlTransaction = db.getDtlTransaction(merchantId);
        view.attachTransaction(dtlTransaction, dtlMerchant.getDefaultCurrency());
        view.attachDtPoints(Double.valueOf(dtlTransaction.getPoints()).intValue());
    }

    public void rescan() {
        photoUploadingManagerS3.cancelUploading(dtlTransaction.getUploadTask());
        dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction)
                .withUploadTask(null);
        db.saveDtlTransaction(merchantId, dtlTransaction);
        //
        view.openScanReceipt(dtlTransaction);
    }

    public void scanQr() {
        dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction)
                .withIsVerified(true);
        //
        db.saveDtlTransaction(merchantId, dtlTransaction);
        //
        view.openScanQr(dtlTransaction);
    }

    public interface View extends Presenter.View {

        void attachDtPoints(int count);

        void attachTransaction(DtlTransaction dtlTransaction, DtlCurrency dtlCurrency);

        void openScanReceipt(DtlTransaction dtlTransaction);

        void openScanQr(DtlTransaction dtlTransaction);
    }
}
