package com.worldventures.dreamtrips.modules.dtl.model.mapping;


import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

import java.util.List;

import rx.Observable;

public class ThinMerchantsTransformer implements Observable.Transformer<List<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant>, List<ThinMerchant>> {

   private final Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant, ThinMerchant> mapper;

   public static final ThinMerchantsTransformer INSTANCE = new ThinMerchantsTransformer();

   public ThinMerchantsTransformer() {
      this.mapper = ThinMerchantMapper.INSTANCE;
   }

   public ThinMerchantsTransformer(Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant, ThinMerchant> mapper) {
      this.mapper = mapper;
   }

   @Override
   public Observable<List<ThinMerchant>> call(Observable<List<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant>> listObservable) {
      return listObservable.flatMap(Observable::from).map(mapper::convert).toList();
   }
}
