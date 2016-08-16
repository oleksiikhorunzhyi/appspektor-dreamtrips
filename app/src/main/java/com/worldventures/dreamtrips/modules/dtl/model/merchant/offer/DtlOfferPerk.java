package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlOfferPerk extends DtlOffer {

    public DtlOfferPerk() {
    }

    public DtlOfferPerk(com.worldventures.dreamtrips.api.dtl.merchants.model.Offer offer) {
        super(offer);
    }

    @Override
    public Type getType() {
        return Type.PERK;
    }
}
