package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import java.util.List;

public class DtlOfferPointsData extends DtlOfferData {

    List<DtlCurrency> currencies;


    public List<DtlCurrency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<DtlCurrency> currencies) {
        this.currencies = currencies;
    }

    @Override
    public String getType() {
        return Offer.POINT_REWARD;
    }
}
