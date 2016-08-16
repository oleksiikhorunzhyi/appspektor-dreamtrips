package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.api.dtl.merchants.model.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import rx.Observable;

public class MerchantTransformer implements Observable.Transformer<Merchant, DtlMerchant> {

    @Override
    public Observable<DtlMerchant> call(Observable<Merchant> merchantObservable) {
        return merchantObservable.map(DtlMerchant::new);
    }
}
