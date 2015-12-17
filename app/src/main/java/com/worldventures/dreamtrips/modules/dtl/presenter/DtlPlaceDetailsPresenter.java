package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.TogglePlaceSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

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

    public DtlPlaceDetailsPresenter(DtlMerchant place) {
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

        if (dtlTransaction != null) {
            checkSucceedEvent();
            checkTransactionOutOfDate();
        }
        //
        view.setTransaction(dtlTransaction);
    }

    private void checkSucceedEvent() {
        DtlTransactionSucceedEvent event = eventBus.getStickyEvent(DtlTransactionSucceedEvent.class);
        if (event != null) {
            eventBus.removeStickyEvent(event);
            view.showSucceed(place, dtlTransaction);
        }
    }

    private void checkTransactionOutOfDate() {
        if (dtlTransaction.outOfDate(Calendar.getInstance().getTimeInMillis())) {
            snapper.deleteDtlTransaction(place.getId());
            dtlTransaction = null;
        }
    }

    public void onCheckInClicked() {
        if (dtlTransaction != null) {
            // we should clean transaction, as for now we don't allow user to save his progress
            // in the enrollment wizard(maybe needed in future)
            if (dtlTransaction.getUploadTask() != null)
                photoUploadingSpiceManager.cancelUploading(dtlTransaction.getUploadTask());
            snapper.cleanDtlTransaction(place.getId(), dtlTransaction);
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
        dtlTransaction.setLat(location.getLatitude());
        dtlTransaction.setLng(location.getLongitude());

        snapper.saveDtlTransaction(place.getId(), dtlTransaction);
        view.setTransaction(dtlTransaction);
        TrackingHelper.dtlCheckin(place.getId());
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
        eventBus.post(new TogglePlaceSelectionEvent(place));
        //
        DtlLocation dtlLocation = snapper.getSelectedDtlLocation();
        view.openMap(new PlacesBundle(dtlLocation));
    }

    /**
     * Analytic-related
     */
    public void trackScreen() {
        String placeTypeAction = place.hasNoOffers() ?
                TrackingHelper.DTL_ACTION_DINING_VIEW : TrackingHelper.DTL_ACTION_OFFER_VIEW;
        TrackingHelper.dtlPlaceView(placeTypeAction, place.getId());
    }

    /**
     * Analytic-related
     */
    public void trackSharing(@ShareFragment.ShareType String type) {
        TrackingHelper.dtlShare(type);
    }

    /**
     * Analytic-related
     */
    public void trackPointEstimator() {
        TrackingHelper.dtlPointsEstimationView();
    }

    /**
     * Analytic-related
     */
    public void trackEarnFlowView() {
        TrackingHelper.dtlEarnView();
    }

    public void routeToPlaceRequested(@Nullable final Intent intent) {
        view.bind(locationDelegate.getLastKnownLocation().compose(new IoToMainComposer<>()))
                .subscribe(location -> {
                    TrackingHelper.dtlMapDestination(location.getLatitude(), location.getLongitude(),
                            place.getCoordinates().getLat(), place.getCoordinates().getLng());
                    TrackingHelper.dtlDirectionsView();
                    view.showMerchantMap(intent);
                }, e -> {
                    TrackingHelper.dtlMapDestination(null, null,
                            place.getCoordinates().getLat(), place.getCoordinates().getLng());
                    TrackingHelper.dtlDirectionsView();
                    view.showMerchantMap(intent);
                    Timber.e(e, "Something went wrong while location update for analytics. Possibly GPS is off");
                });
    }

    public interface View extends DtlPlaceCommonDetailsPresenter.View, RxView {

        void showEstimationDialog(PointsEstimationDialogBundle data);

        void openSuggestMerchant(SuggestPlaceBundle data);

        void openTransaction(DtlMerchant DtlMerchant, DtlTransaction dtlTransaction);

        void showSucceed(DtlMerchant DtlMerchant, DtlTransaction dtlTransaction);

        void openMap(PlacesBundle placesBundle);

        void setTransaction(DtlTransaction dtlTransaction);

        void setSuggestMerchantButtonAvailable(boolean available);

        void share(DtlMerchant place);

        void resolutionRequired(Status status);

        void enableCheckinButton();

        void disableCheckinButton();

        void showMerchantMap(@Nullable Intent intent);
    }
}
