package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;

import javax.inject.Inject;

public class DtlTransactionSucceedPresenter extends Presenter<DtlTransactionSucceedPresenter.View> {

    private final DtlPlace dtlPlace;

    @Inject
    SnappyRepository snapper;

    public DtlTransactionSucceedPresenter(DtlPlace dtlPlace) {
        this.dtlPlace = dtlPlace;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        DtlTransaction dtlTransaction = snapper.getDtlTransaction(dtlPlace.getId());
        view.setCongratulations(dtlTransaction.getDtlTransactionResult());
    }

    public interface View extends Presenter.View {
        void setCongratulations(DtlTransactionResult result);
    }
}
