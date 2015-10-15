package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.v4.app.FragmentManager;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceMedia;
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

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setPlace(place);
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

    public void onEstimationClick(FragmentManager fm) {
        NavigationBuilder.create()
                .forDialog(fm)
                .data(new PointsEstimationDialogBundle(place.getId()))
                .move(Route.DTL_POINTS_ESTIMATION);
    }

    public void onSuggestMerchantClicked() {
        view.toSuggestMerchant(place);
    }

    public void onMerchantClick() {
        NavigationBuilder.create()
                .with(activityRouter)
                .move(Route.DTL_SUGGEST_MERCHANT);
    }

    public void onShareClick() {
        new ShareDialog(activityRouter.getContext(), type -> {
            ShareBundle shareBundle = new ShareBundle();
            shareBundle.setShareType(type);
            shareBundle.setText(context.getString(R.string.dtl_details_share_title, place.getName()));
            shareBundle.setShareUrl(place.getWebsite());
            DtlPlaceMedia media = Queryable.from(place.getMediaList()).firstOrDefault();
            if (media != null) shareBundle.setImageUrl(media.getImagePath());
            NavigationBuilder.create()
                    .with(activityRouter)
                    .data(shareBundle)
                    .move(Route.SHARE);
        }).show();
    }

    public interface View extends Presenter.View {
        void openTransaction(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void setPlace(DtlPlace place);

        void setTransaction(DtlTransaction dtlTransaction);

        void toSuggestMerchant(DtlPlace place);
    }
}
