package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.v4.app.FragmentManager;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.SuggestPlaceBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceMedia;
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

    public void onEstimationClick(FragmentManager fm) {
        NavigationBuilder.create()
                .forDialog(fm)
                .data(new PointsEstimationDialogBundle(place.getMerchantId()))
                .move(Route.DTL_POINTS_ESTIMATION);
    }

    public void onMerchantClick() {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(new SuggestPlaceBundle(place))
                .move(Route.DTL_SUGGEST_MERCHANT);
    }

    public void onShareClick() {
        new ShareDialog(activityRouter.getContext(), type -> {
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(context.getString(R.string.dtl_details_share_title, place.getDisplayName()));
            shareBundle.setShareUrl(place.getWebsite());
            DtlPlaceMedia media = Queryable.from(place.getImages()).firstOrDefault();
            if (media != null) shareBundle.setImageUrl(media.getImagePath());
            NavigationBuilder.create()
                    .with(activityRouter)
                    .data(shareBundle)
                    .move(Route.SHARE);
        }).show();
    }

    public void onBackPressed() {
        DtlLocation dtlLocation = snapper.getSelectedDtlLocation();
        view.openMap(new PlacesBundle(dtlLocation));
    }

    public interface View extends DtlPlaceCommonDetailsPresenter.View {
        void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void openMap(PlacesBundle placesBundle);

        void setTransaction(DtlTransaction dtlTransaction);

        void setSuggestMerchantButtonAvailable(boolean available);
    }
}
