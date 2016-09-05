package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantBackward;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import rx.Observable;

public class MerchantTransformer implements Observable.Transformer<MerchantBackward, DtlMerchant> {

   public static final MerchantTransformer INSTANCE = new MerchantTransformer();

   @Override
   public Observable<DtlMerchant> call(Observable<MerchantBackward> merchantObservable) {
      return merchantObservable.map(DtlMerchant::new);
   }
}
