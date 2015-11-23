package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;

import java.util.Calendar;

import javax.inject.Inject;

public class DtlPlaceDetailsPresenter extends DtlPlaceCommonDetailsPresenter<DtlPlaceDetailsPresenter.View> {

    private DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;
    @Inject
    FeatureManager featureManager;

    public DtlPlaceDetailsPresenter(DtlPlace place) {
        super(place);
    }

    @Override
    public void onResume() {
        super.onResume();
        processTransaction();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (place.getPartnerStatus() == DtlPlaceType.DINING)
            featureManager.with(Feature.REP_SUGGEST_MERCHANT, () -> view.setSuggestMerchantButtonAvailable(true),
                    () -> view.setSuggestMerchantButtonAvailable(false));
    }

    private void processTransaction() {
        dtlTransaction = snapper.getDtlTransaction(place.getMerchantId());

        if (dtlTransaction != null
                && !checkSucceedEvent()
                && !checkTransactionOutOfDate()) {
            // we should clean transaction, as for now we don't allow user to save his progress
            // in the enrollment wizard(maybe needed in future)
            if (dtlTransaction.getUploadTask() != null)
                photoUploadingSpiceManager.cancelUploading(dtlTransaction.getUploadTask());
            snapper.cleanDtlTransaction(place.getMerchantId(), dtlTransaction);
        }
        //
        view.setTransaction(dtlTransaction);
    }

    private boolean checkSucceedEvent() {
        DtlTransactionSucceedEvent event = eventBus.getStickyEvent(DtlTransactionSucceedEvent.class);
        if (event != null) {
            eventBus.removeStickyEvent(event);
            view.openTransaction(place, dtlTransaction);
            return true;
        } else return false;
    }

    private boolean checkTransactionOutOfDate() {
        if (dtlTransaction != null && dtlTransaction.outOfDate(Calendar.getInstance().getTimeInMillis())) {
            snapper.deleteDtlTransaction(place.getMerchantId());
            dtlTransaction = null;
            return true;
        } else return false;
    }

    public void onCheckInClicked() {
        if (dtlTransaction != null) {
            view.openTransaction(place, dtlTransaction);
        } else {
            dtlTransaction = new DtlTransaction();
            dtlTransaction.setTimestamp(Calendar.getInstance().getTimeInMillis());

            snapper.saveDtlTransaction(place.getMerchantId(), dtlTransaction);
            view.setTransaction(dtlTransaction);
        }
    }

    public void onEstimationClick() {
        view.showEstimationDialog(new PointsEstimationDialogBundle(place.getMerchantId()));
    }

    public void onMerchantClick() {
        view.openSuggestMerchant(new SuggestPlaceBundle(place));
    }

    public void onShareClick() {
        view.share(place);
    }

    public void onBackPressed() {
        DtlLocation dtlLocation = snapper.getSelectedDtlLocation();
        view.openMap(new PlacesBundle(dtlLocation));
    }

    public interface View extends DtlPlaceCommonDetailsPresenter.View {

        void showEstimationDialog(PointsEstimationDialogBundle data);

        void openSuggestMerchant(SuggestPlaceBundle data);

        void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void openMap(PlacesBundle placesBundle);

        void setTransaction(DtlTransaction dtlTransaction);

        void setSuggestMerchantButtonAvailable(boolean available);

        void share(DtlPlace place);
    }
}
