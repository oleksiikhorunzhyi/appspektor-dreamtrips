package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.dtl.event.TogglePlaceSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class DtlMerchantListPresenter extends DtlMerchantsPresenter<DtlMerchantListPresenter.View> {

    @Inject
    SnappyRepository db;

    public DtlMerchantListPresenter(DtlMerchantType dtlMerchantType) {
        this.dtlMerchantType = dtlMerchantType;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlLocation = db.getSelectedDtlLocation();
        //
        view.showProgress();
        //
        performFiltering();
        //
        if (dtlMerchantType == DtlMerchantType.OFFER) view.setComingSoon();
    }

    @Override
    public void onMerchantsFailed(SpiceException spiceException) {
        view.hideProgress();
    }

    @Override
    protected void afterMapping(List<DtlMerchant> merchants) {
        super.afterMapping(merchants);
        Collections.sort(merchants, DtlMerchant.DISTANCE_COMPARATOR);
    }

    @Override
    protected void merchantsPrepared(List<DtlMerchant> dtlMerchants) {
        view.setItems(dtlMerchants);
    }

    public void onEventMainThread(TogglePlaceSelectionEvent event) {
        view.toggleSelection(event.getDtlMerchant());
    }

    public interface View extends RxView {

        void setItems(List<DtlMerchant> dtlMerchants);

        void showProgress();

        void hideProgress();

        void toggleSelection(DtlMerchant DtlMerchant);

        void setComingSoon();
    }
}
