package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.ImmutableCurrency;

public class CurrencyMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Currency, Currency> {

   public static final CurrencyMapper INSTANCE = new CurrencyMapper();

   @Override
   public Currency convert(com.worldventures.dreamtrips.api.dtl.merchants.model.Currency source) {
      return ImmutableCurrency.builder()
            .code(source.code())
            .name(source.name())
            .prefix(source.prefix())
            .suffix(source.suffix())
            .isDefault(source.isDefault())
            .build();
   }
}
