package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.ImmutableCurrency;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class CurrencyConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Currency, Currency> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.Currency> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.Currency.class;
   }

   @Override
   public Class<Currency> targetClass() {
      return Currency.class;
   }

   @Override
   public Currency convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.Currency currency) {
      return ImmutableCurrency.builder()
            .code(currency.code())
            .name(currency.name())
            .prefix(currency.prefix())
            .suffix(currency.suffix())
            .isDefault(currency.isDefault())
            .build();
   }
}
