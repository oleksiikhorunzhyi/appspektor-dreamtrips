package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionLocation;

import java.util.Calendar;

import javax.inject.Inject;

import timber.log.Timber;

public class DtlPlaceDetailsPresenter extends DtlPlaceCommonDetailsPresenter<DtlPlaceDetailsPresenter.View> {

    private DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;
    @Inject
    FeatureManager featureManager;
    @Inject
    LocationDelegate locationDelegate;

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
        if (place.hasNoOffers())
            featureManager.with(Feature.REP_SUGGEST_MERCHANT, () -> view.setSuggestMerchantButtonAvailable(true),
                    () -> view.setSuggestMerchantButtonAvailable(false));
    }

    private void processTransaction() {
        dtlTransaction = snapper.getDtlTransaction(place.getId());

        if (dtlTransaction != null
                && !checkSucceedEvent()
                && !checkTransactionOutOfDate()) {
            // we should clean transaction, as for now we don't allow user to save his progress
            // in the enrollment wizard(maybe needed in future)
            if (dtlTransaction.getUploadTask() != null)
                photoUploadingSpiceManager.cancelUploading(dtlTransaction.getUploadTask());
            snapper.cleanDtlTransaction(place.getId(), dtlTransaction);
        }
        //
        view.setTransaction(dtlTransaction);
    }

    private boolean checkSucceedEvent() {
        DtlTransactionSucceedEvent event = eventBus.getStickyEvent(DtlTransactionSucceedEvent.class);
        if (event != null) {
            eventBus.removeStickyEvent(event);
            view.showSucceed(place, dtlTransaction);
            return true;
        } else return false;
    }

    private boolean checkTransactionOutOfDate() {
        if (dtlTransaction != null && dtlTransaction.outOfDate(Calendar.getInstance().getTimeInMillis())) {
            snapper.deleteDtlTransaction(place.getId());
            dtlTransaction = null;
            return true;
        } else return false;
    }

    public void onCheckInClicked() {
        if (dtlTransaction != null) {
            view.openTransaction(place, dtlTransaction);
        } else {
            view.disableCheckinButton();
            view.bind(locationDelegate
                            .requestLocationUpdate()
                            .compose(new IoToMainComposer<>())
            ).subscribe(this::onLocationObtained, this::onLocationError);
        }
    }

    public void locationNotGranted() {
        view.enableCheckinButton();
        view.informUser(R.string.dtl_checkin_location_error);
    }

    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            onStatusError(((LocationDelegate.LocationException) e).getStatus());
        else {
            locationNotGranted();
            Timber.e(e, "Something went wrong while location update");
        }
    }

    private void onStatusError(Status status) {
        view.resolutionRequired(status);
    }

    private void onLocationObtained(Location location) {
        view.enableCheckinButton();
        dtlTransaction = new DtlTransaction();
        dtlTransaction.setTimestamp(Calendar.getInstance().getTimeInMillis());
        dtlTransaction.setLocation(DtlTransactionLocation.fromDtlPlace(place,
                location.getLatitude(), location.getLongitude()));

        snapper.saveDtlTransaction(place.getId(), dtlTransaction);
        view.setTransaction(dtlTransaction);
    }

    public void onEstimationClick() {
        view.showEstimationDialog(new PointsEstimationDialogBundle(place.getId()));
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

    public interface View extends DtlPlaceCommonDetailsPresenter.View, RxView {

        void showEstimationDialog(PointsEstimationDialogBundle data);

        void openSuggestMerchant(SuggestPlaceBundle data);

        void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void showSucceed(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void openMap(PlacesBundle placesBundle);

        void setTransaction(DtlTransaction dtlTransaction);

        void setSuggestMerchantButtonAvailable(boolean available);

        void share(DtlPlace place);

        void resolutionRequired(Status status);

        void enableCheckinButton();

        void disableCheckinButton();
    }
}
