package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantStore;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.Calendar;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class DtlDetailsPresenterImpl extends DtlPresenterImpl<DtlDetailsScreen, ViewState.EMPTY>
        implements DtlDetailsPresenter {

    @Inject
    DtlMerchantStore merchantStore;
    @Inject
    SnappyRepository db;
    @Inject
    FeatureManager featureManager;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    protected PhotoUploadingManagerS3 photoUploadingManagerS3;
    //
    private DtlTransaction dtlTransaction;
    protected DtlMerchant merchant;
    protected final String merchantId;
    protected final DtlOfferData expandOffer;


    public DtlDetailsPresenterImpl(Context context, Injector injector, String merchantId, DtlOfferData expandOffer) {
        super(context);
        injector.inject(this);
        this.expandOffer = expandOffer;
        this.merchantId = merchantId;
        merchantStore.getMerchantById(merchantId)
                .compose(ImmediateComposer.instance())
                .subscribe(value -> merchant = value);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //
        getView().setMerchant(merchant, expandOffer);
        //
        if (merchant.hasNoOffers()) getView().setSuggestMerchantButtonAvailable(featureManager.available(Feature.REP_SUGGEST_MERCHANT));
        else processTransaction();
    }

    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_detailed_merchant;
    }

    @Override public boolean onToolbarMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_share) onShareClick();
        return super.onToolbarMenuItemClick(item);
    }

    private void processTransaction() {
        dtlTransaction = db.getDtlTransaction(merchant.getId());
        //
        if (dtlTransaction != null) {
            checkSucceedEvent();
            checkTransactionOutOfDate();
        }
        //
        getView().setTransaction(dtlTransaction);
    }

    private void checkSucceedEvent() {
        DtlTransactionSucceedEvent event = EventBus.getDefault().getStickyEvent(DtlTransactionSucceedEvent.class);
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
            getView().showSucceed(merchant, dtlTransaction);
        }
    }

    private void checkTransactionOutOfDate() {
        if (dtlTransaction.isOutOfDate(Calendar.getInstance().getTimeInMillis())) {
            db.deleteDtlTransaction(merchant.getId());
            dtlTransaction = null;
        }
    }

    @Override
    public void onCheckInClicked() {
        if (dtlTransaction != null) {
            // we should clean transaction, as for now we don't allow user to save his progress
            // in the enrollment wizard(maybe needed in future)
            // NOTE! :: but we save checkin (coordinates and checkin time)
            if (dtlTransaction.getUploadTask() != null)
                photoUploadingManagerS3.cancelUploading(dtlTransaction.getUploadTask());
            db.cleanDtlTransaction(merchant.getId(), dtlTransaction);
            getView().openTransaction(merchant, dtlTransaction);
            TrackingHelper.dtlEarnView();
        } else {
            getView().disableCheckinButton();
            locationDelegate.requestLocationUpdate()
                    .compose(bindViewIoToMainComposer())
                    .subscribe(this::onLocationObtained, this::onLocationError);
        }
    }

    public void locationNotGranted() {
        getView().enableCheckinButton();
        getView().informUser(R.string.dtl_checkin_location_error);
    }

    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
        else {
            locationNotGranted();
            Timber.e(e, "Something went wrong while location update");
        }
    }

    private void onLocationObtained(Location location) {
        getView().enableCheckinButton();
        //
        dtlTransaction = ImmutableDtlTransaction.builder()
                .lat(location.getLatitude())
                .lng(location.getLongitude())
                .build();
        db.saveDtlTransaction(merchant.getId(), dtlTransaction);
        //
        getView().setTransaction(dtlTransaction);
        //
        TrackingHelper.dtlCheckin(merchant.getId());
    }

    @Override
    public void onEstimationClick() {
        getView().showEstimationDialog(new PointsEstimationDialogBundle(merchant.getId()));
    }

    @Override
    public void onMerchantClick() {
        getView().openSuggestMerchant(new MerchantIdBundle(merchant.getId()));
    }

    public void onShareClick() {
        getView().share(merchant);
    }

    /**
     * Analytic-related
     */
    @Override
    public void trackScreen() {
        if (merchant == null) return;
        //
        String merchantTypeAction = merchant.hasNoOffers() ?
                TrackingHelper.DTL_ACTION_DINING_VIEW : TrackingHelper.DTL_ACTION_OFFER_VIEW;
        TrackingHelper.dtlMerchantView(merchantTypeAction, merchant.getId());
    }

    /**
     * Analytic-related
     */
    @Override
    public void trackSharing(@ShareType String type) {
        TrackingHelper.dtlShare(type);
    }

    /**
     * Analytic-related
     */
    @Override
    public void trackPointEstimator() {
        TrackingHelper.dtlPointsEstimationView();
    }

    @Override
    public void routeToMerchantRequested(@Nullable final Intent intent) {
        locationDelegate.getLastKnownLocation()
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> {
                    TrackingHelper.dtlMapDestination(location.getLatitude(), location.getLongitude(),
                            merchant.getCoordinates().getLat(), merchant.getCoordinates().getLng());
                    TrackingHelper.dtlDirectionsView();
                    getView().showMerchantMap(intent);
                }, e -> {
                    TrackingHelper.dtlMapDestination(null, null,
                            merchant.getCoordinates().getLat(), merchant.getCoordinates().getLng());
                    TrackingHelper.dtlDirectionsView();
                    getView().showMerchantMap(intent);
                    Timber.e(e, "Something went wrong while location update for analytics. Possibly GPS is off");
                });
    }
}
