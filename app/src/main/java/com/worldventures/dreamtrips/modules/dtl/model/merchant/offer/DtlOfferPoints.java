package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlOfferPoints extends DtlOffer {

    List<DtlCurrency> currencies;

    public DtlOfferPoints() {
    }

    public DtlOfferPoints(com.worldventures.dreamtrips.api.dtl.merchats.model.Offer offer) {
        super(offer);
    }

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
