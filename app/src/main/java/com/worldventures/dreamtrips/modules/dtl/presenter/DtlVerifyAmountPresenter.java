package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlPlacePointsEstimationQuery;
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

    public void rescan() {
        photoUploadingSpiceManager.cancelUploading(dtlTransaction.getUploadTask());
        dtlTransaction.setUploadTask(null);

        snapper.saveDtlTransaction(dtlPlace.getMerchantId(), dtlTransaction);

        view.openScanReceipt();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(dtlPlace.getMerchantId());
        view.attachTransaction(dtlTransaction);

        doRequest(new GetDtlPlacePointsEstimationQuery(dtlPlace.getMerchantId(), dtlTransaction.getAmount()),
                points -> view.attachDtPoints(points.intValue()));
    }

    public interface View extends Presenter.View {
        void attachTransaction(DtlTransaction dtlTransaction);

        void attachDtPoints(int count);

        void openScanReceipt();
    }
}
