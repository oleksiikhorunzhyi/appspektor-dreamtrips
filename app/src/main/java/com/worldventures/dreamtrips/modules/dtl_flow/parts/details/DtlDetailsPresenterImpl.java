package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionService;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;

import java.util.Calendar;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class DtlDetailsPresenterImpl extends DtlPresenterImpl<DtlDetailsScreen, ViewState.EMPTY>
        implements DtlDetailsPresenter {

    @Inject
    DtlMerchantService merchantService;
    @Inject
    FeatureManager featureManager;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlTransactionService transactionService;
    @Inject
    protected PhotoUploadingManagerS3 photoUploadingManagerS3;
    //
    protected DtlMerchant merchant;
    protected final String merchantId;
    protected final DtlOfferData expandOffer;


    public DtlDetailsPresenterImpl(Context context, Injector injector, String merchantId, DtlOfferData expandOffer) {
        super(context);
        injector.inject(this);
        this.expandOffer = expandOffer;
        this.merchantId = merchantId;
        merchantService.merchantByIdPipe()
                .createObservable(new DtlMerchantByIdAction(merchantId))
                .compose(ImmediateComposer.instance())
                .subscribe(new ActionStateSubscriber<DtlMerchantByIdAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> merchant = action.getResult()));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //
        getView().setMerchant(merchant, expandOffer);
        //
        if (merchant.hasNoOffers()) {
            getView().setSuggestMerchantButtonAvailable(
                    featureManager.available(Feature.REP_SUGGEST_MERCHANT));
        } else processTransaction();
    }

    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_detailed_merchant;
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_share) onShareClick();
        return super.onToolbarMenuItemClick(item);
    }

    private void processTransaction() {
        transactionService.transactionActionPipe()
                .createObservable(DtlTransactionAction.get(merchant))
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> {
                            DtlTransaction transaction = action.getResult();
                            if (transaction != null) {
                                checkSucceedEvent(transaction);
                                checkTransactionOutOfDate(transaction);
                            }
                            getView().setTransaction(transaction);

                        }));

    }

    private void checkSucceedEvent(DtlTransaction transaction) {
        DtlTransactionSucceedEvent event = EventBus.getDefault().getStickyEvent(DtlTransactionSucceedEvent.class);
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
            getView().showSucceed(merchant, transaction);
        }
    }

    private void checkTransactionOutOfDate(DtlTransaction transaction) {
        if (transaction.isOutOfDate(Calendar.getInstance().getTimeInMillis())) {
            transactionService.transactionActionPipe().send(DtlTransactionAction.delete(merchant));
        }
    }

    @Override
    public void onCheckInClicked() {
        transactionService.transactionActionPipe()
                .createObservable(DtlTransactionAction.get(merchant))
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> {
                            if (action.getResult() != null) {
                                DtlTransaction dtlTransaction = action.getResult();
                                photoUploadingManagerS3.cancelUploading(dtlTransaction.getUploadTask());
                                transactionService.transactionActionPipe().send(DtlTransactionAction.clean(merchant));
                                getView().openTransaction(merchant, dtlTransaction);
                                TrackingHelper.dtlEarnView();
                            } else {
                                getView().disableCheckinButton();
                                locationDelegate.requestLocationUpdate()
                                        .compose(bindViewIoToMainComposer())
                                        .subscribe(this::onLocationObtained, this::onLocationError);
                            }
                        }));
    }

    @Override
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
        DtlTransaction dtlTransaction = ImmutableDtlTransaction.builder()
                .lat(location.getLatitude())
                .lng(location.getLongitude())
                .build();
        transactionService.transactionActionPipe().send(DtlTransactionAction.save(merchant, dtlTransaction));
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

    @Override
    public void onOfferClick(DtlOfferData offer) {
        DtlOfferMedia imageUrl = Queryable.from(offer.getImages()).firstOrDefault();
        if (imageUrl == null) return;
        Flow.get(getContext()).set(new DtlFullscreenImagePath(imageUrl.getUrl()));
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
