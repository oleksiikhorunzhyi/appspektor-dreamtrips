package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerkData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsData;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlMerchantsPresenter extends DtlPresenter<DtlMerchantsScreen, ViewState.EMPTY> {

    void locationChangeRequested();

    void applySearch(String query);

    void merchantClicked(DtlMerchant merchant);

    void perkClick(DtlOfferPerkData perk);

    void pointClicked(DtlOfferPointsData points);

    void mapClicked();
}
