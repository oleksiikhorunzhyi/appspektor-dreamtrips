package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;

import javax.inject.Inject;

public class DtlVerifyAmountPresenter extends Presenter<DtlVerifyAmountPresenter.View> {

    @Inject
    SnappyRepository snapper;

    private final DtlPlace dtlPlace;
    private DtlTransaction dtlTransaction;

    public DtlVerifyAmountPresenter(DtlPlace place) {
        this.dtlPlace = place;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(dtlPlace.getMerchantId());
        view.attachTransaction(dtlTransaction);
        view.attachDtPoints(Double.valueOf(dtlTransaction.getPoints()).intValue());
    }

    public void rescan() {
        photoUploadingSpiceManager.cancelUploading(dtlTransaction.getUploadTask());
        dtlTransaction.setUploadTask(null);

        snapper.saveDtlTransaction(dtlPlace.getMerchantId(), dtlTransaction);

        view.openScanReceipt(dtlTransaction);
    }

    public void scanQr() {
        dtlTransaction.setVerified(true);

        snapper.saveDtlTransaction(dtlPlace.getMerchantId(), dtlTransaction);

        view.openScanQr(dtlTransaction);
    }

    public interface View extends Presenter.View {
        void attachTransaction(DtlTransaction dtlTransaction);

        void attachDtPoints(int count);

        void openScanReceipt(DtlTransaction dtlTransaction);

        void openScanQr(DtlTransaction dtlTransaction);
    }
}
