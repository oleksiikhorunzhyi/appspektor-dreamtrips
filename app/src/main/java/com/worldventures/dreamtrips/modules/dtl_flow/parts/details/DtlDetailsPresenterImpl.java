package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.dtl.analytics.CheckinEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantMapDestinationEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.PointsEstimatorViewEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.SuggestMerchantEvent;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class DtlDetailsPresenterImpl extends DtlPresenterImpl<DtlDetailsScreen, DtlMerchantDetailsState>
        implements DtlDetailsPresenter {

    @Inject
    FeatureManager featureManager;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlMerchantInteractor merchantInteractor;
    @Inject
    DtlTransactionInteractor transactionInteractor;
    @Inject
    protected PhotoUploadingManagerS3 photoUploadingManagerS3;
    //
    protected DtlMerchant merchant;
    protected List<Integer> preExpandOffers;

    public DtlDetailsPresenterImpl(Context context, Injector injector, DtlMerchant merchant, List<Integer> preExpandOffers) {
        super(context);
        injector.inject(this);
        this.merchant = merchant;
        this.preExpandOffers = preExpandOffers;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(new MerchantDetailsViewCommand(new MerchantDetailsViewEvent(merchant)));
        //
        getView().setMerchant(merchant);
        getView().expandOffers(getViewState().getOffers() != null ?
                getViewState().getOffers() : preExpandOffers);
        //
        if (merchant.hasNoOffers()) {
            getView().setSuggestMerchantButtonAvailable(
                    featureManager.available(Feature.REP_SUGGEST_MERCHANT));
        } else processTransaction();
    }

    @Override
    public void onNewViewState() {
        state = new DtlMerchantDetailsState();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        state.setOffers(getView().getExpandedOffers());
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            getView().setMap(merchant);
        }
        if (visibility == View.VISIBLE && !merchant.hasNoOffers()) {
            processTransaction();
        }
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
        transactionInteractor.transactionActionPipe()
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
            transactionInteractor.transactionActionPipe()
                    .createObservable(DtlTransactionAction.delete(merchant))
                    .compose(bindViewIoToMainComposer())
                    .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                            .onFail(apiErrorPresenter::handleActionError)
                            .onSuccess(action -> getView().setTransaction(action.getResult())));
        }
    }

    @Override
    public void onCheckInClicked() {
        transactionInteractor.transactionActionPipe()
                .createObservable(DtlTransactionAction.get(merchant))
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> {
                            if (action.getResult() != null) {
                                DtlTransaction dtlTransaction = action.getResult();
                                if (dtlTransaction.getUploadTask() != null) {
                                    photoUploadingManagerS3.cancelUploading(dtlTransaction.getUploadTask());
                                }
                                transactionInteractor.transactionActionPipe().send(DtlTransactionAction.clean(merchant));
                                getView().openTransaction(merchant, dtlTransaction);
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
        transactionInteractor.transactionActionPipe()
                .send(DtlTransactionAction.save(merchant, dtlTransaction));
        //
        getView().setTransaction(dtlTransaction);
        //
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(DtlAnalyticsCommand.create(new CheckinEvent(merchant)));
    }

    @Override
    public void onEstimationClick() {
        getView().showEstimationDialog(new PointsEstimationDialogBundle(merchant.getId()));
    }

    @Override
    public void onMerchantClick() {
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(DtlAnalyticsCommand.create(new SuggestMerchantEvent(merchant)));
        getView().openSuggestMerchant(new MerchantIdBundle(merchant.getId()));
    }

    @Override
    public void onOfferClick(DtlOffer offer) {
        DtlOfferMedia imageUrl = Queryable.from(offer.getImages()).firstOrDefault();
        if (imageUrl == null) return;
        Flow.get(getContext()).set(new DtlFullscreenImagePath(imageUrl.getUrl()));
    }

    public void onShareClick() {
        getView().share(merchant);
    }

    @Override
    public void trackSharing(@ShareType String type) {
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(DtlAnalyticsCommand.create(
                        ShareEventProvider.provideMerchantShareEvent(merchant, type)));
    }

    @Override
    public void trackPointEstimator() {
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(DtlAnalyticsCommand.create(new PointsEstimatorViewEvent(merchant)));
    }

    @Override
    public void routeToMerchantRequested(@Nullable final Intent intent) {
        locationDelegate.getLastKnownLocation()
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> {
                    analyticsInteractor.dtlAnalyticsCommandPipe()
                            .send(DtlAnalyticsCommand.create(
                                    new MerchantMapDestinationEvent(location, merchant)));
                    getView().showMerchantMap(intent);
                }, e -> {
                    analyticsInteractor.dtlAnalyticsCommandPipe()
                            .send(DtlAnalyticsCommand.create(
                                    new MerchantMapDestinationEvent(null, merchant)));
                    getView().showMerchantMap(intent);
                });
    }
}