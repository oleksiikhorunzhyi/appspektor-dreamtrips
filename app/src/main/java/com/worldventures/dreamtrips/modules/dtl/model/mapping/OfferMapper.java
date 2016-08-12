package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchats.model.Offer;
import com.worldventures.dreamtrips.api.dtl.merchats.model.OfferType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerk;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPoints;

public class OfferMapper implements Mapper<Offer, DtlOffer> {

    @Override
    public DtlOffer map(Offer source) {
        if (source.type() == OfferType.PERK) {
            return new DtlOfferPerk(source);
        } else {
            DtlOfferPoints offer = new DtlOfferPoints(source);
            offer.setCurrencies(Queryable.from(source.offerData().currencies())
                    .map(DtlCurrency::new)
                    .toList());
            return offer;
        }
    }
}
