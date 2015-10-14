package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;

import java.util.Calendar;

import javax.inject.Inject;

public class DtlPlaceDetailsPresenter extends Presenter<DtlPlaceDetailsPresenter.View> {

    private final DtlPlace place;

    private DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;

    public DtlPlaceDetailsPresenter(DtlPlace place) {
        this.place = place;
    }

    public void onCheckInClicked() {
        if (dtlTransaction != null) {
            view.openTransaction(place, dtlTransaction);
        } else {
            dtlTransaction = new DtlTransaction();
            dtlTransaction.setTimestamp(Calendar.getInstance().getTimeInMillis());
            snapper.saveDtlTransaction(place.getId(), dtlTransaction);
            updateTransactionStatus();
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setPlace(place);
    }

    public void onEstimationClick(FragmentManager fm) {
        NavigationBuilder.create()
                .forDialog(fm)
                .data(new PointsEstimationDialogBundle(place.getId()))
                .move(Route.DTL_POINTS_ESTIMATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        dtlTransaction = snapper.getDtlTransaction(place.getId());
        checkTransaction();
        updateTransactionStatus();
    }

    private void checkTransaction() {
        if (dtlTransaction != null && dtlTransaction.outOfDate(Calendar.getInstance().getTimeInMillis())) {
            snapper.deleteDtlTransaction(place.getId());
            dtlTransaction = null;
            updateTransactionStatus();
        }
    }

    private void updateTransactionStatus() {
        view.setTransaction(dtlTransaction);
    }

    public interface View extends Presenter.View {
        void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void setPlace(DtlPlace place);

        void setTransaction(DtlTransaction dtlTransaction);
    }
}
