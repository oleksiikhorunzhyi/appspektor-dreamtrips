package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import java.util.List;

public class DtlOfferPoints extends DtlOffer {

    List<DtlCurrency> currencies;

    public List<DtlCurrency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<DtlCurrency> currencies) {
        this.currencies = currencies;
    }

    @Override
    public Type getType() {
        return Type.POINTS;
    }
}
